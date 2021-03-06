package com.hexacta.enhanced.authentication

import javax.servlet.RequestDispatcher

import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean
import org.apache.catalina.startup.SetSessionConfig;
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.metaclass.RedirectDynamicMethod
import org.springframework.context.ApplicationContextAware
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.RequestAttributes

import com.hexacta.enhanced.authentication.AuthenticationService
import com.hexacta.enhanced.authentication.AuthenticationUser
import com.hexacta.enhanced.authentication.Component
import com.hexacta.enhanced.authentication.ControllerConfiguration
import com.hexacta.enhanced.authentication.Method
import com.hexacta.enhanced.authentication.Permission
import com.hexacta.enhanced.authentication.Role

import com.hexacta.enhanced.authentication.AuthenticatedUser
import com.hexacta.enhanced.authentication.annotations.Visible

class AuthenticationService {
	protected static final SESSION_KEY_AUTH_USER = 'grails-authentication.authenticatedUser'
	protected static final REQUEST_KEY_AUTH_USER = 'grails-authentication.authenticatedUser'
	// In minutes
	protected static final PASSWORD_RESET_DEFAULT_TIMEOUT = 30
	
	private static ThreadLocal<AuthenticatedUser> processUser = new ThreadLocal<AuthenticatedUser>()

	static nonAuthenticatedActions = [[controller:'authentication', action:'*']] as Set
	static tokenAuthenticatedActions = [] as Set
	
	static userDomainClass = AuthenticationUser
	static roleDomainClass = Role

	def grailsApplication
	@Transactional
	def bootstrap(){
		if(grailsApplication.config?.enhanced?.authentication?.forbidMultipleSessions){
			clearSessionTokens()
		}
		if(!grailsApplication.config?.enhanced?.authentication?.disable?.bootstrap?.controllers){
			createControllers()
		}
		fireEvent("BootstrapRoles", [grailsApplication: grailsApplication])
		fireEvent("BootstrapUsers", [grailsApplication: grailsApplication])
	}
	@Transactional
	def getPermission(conName, methodName){
		def controller = ControllerConfiguration.findByName(conName)
		def method = controller.methods.find { it.name == methodName}
		def permission = Permission.findByControllerAndMethod(controller,method)
		if(!permission){ 
			permission = new Permission()
			permission.controller = controller
			permission.method = method
			permission = permission.save()
		}
		return permission
	}
	@Transactional(readOnly = true)
	def registerComponent(componentId) {
		if(!Component.findByName(componentId)){
			new Component(name: componentId).save()
		}
	}
	
	@Transactional(readOnly = true)
	def getAuthenticationType(controllerName, actionName){
		def matches = {
			(it.controller == controllerName) && ((it.action == '*') || (it.action == actionName))
		}
		def type
		if(nonAuthenticatedActions.find(matches)){
			type = 'noFiltering'
		}
		else if(tokenAuthenticatedActions.find(matches)){
			type = 'byToken'
		}
		else{
			type = 'byCredentials'
		}
		type
	}
	
	@Transactional
	def clearSessionTokens(){
		AuthenticationUser.list().each {
			it.sessionToken = null
			it.save()
		}
	}
	@Transactional
	def createControllers(){
		// Create Controllers
		def all = ControllerConfiguration.findByName(Permission.ALL)
		def controllers = grailsApplication.getArtefacts("Controller")
		controllers = controllers.findAll { it.getClazz().isAnnotationPresent(Visible)}
		controllers.each {
			def conName= it.getLogicalPropertyName()
			if(!ControllerConfiguration.findByName(conName)){
				def conKey =  it.getClazz().getAnnotation(Visible).key()
				new ControllerConfiguration(name: conName, label: conKey).save()
			}
		}
		// Gather Methods
		def methods = [Permission.ALL+":"+Permission.ALL]
		controllers.each {
			methods.addAll(it.getClazz().getMethods().findAll { it.isAnnotationPresent(Visible) }.collect { it.name + ":" + it.getAnnotation(Visible).key()})
		}
		methods = methods as Set
		// Create Methods
		methods.each {
			def tokens = it.tokenize(":")
			def method = Method.findByNameAndLabel(tokens.get(0), tokens.get(1))
			if(!method){
				new Method(name: tokens.get(0), label:  tokens.get(1)).save()
			}
		}
		// Associate Methods
		if(!all){
			all = new ControllerConfiguration(name: Permission.ALL, label:  Permission.ALL)
			[Permission.ALL, "list", "create", "show", "edit", "save", "update", "delete"].each {
				def method = Method.findByNameAndLabel(it, it)
				all.addToMethods(method)
			}
			all.save()
		}
		controllers.each {
			def controller = ControllerConfiguration.findByName(it.getLogicalPropertyName())
			def allMethod = Method.findByNameAndLabel(Permission.ALL,  Permission.ALL)
			controller.addToMethods(allMethod)
			it.getClazz().getMethods().findAll { it.isAnnotationPresent(Visible) }.each {
				def method = Method.findByNameAndLabel(it.name,  it.getAnnotation(Visible).key())
				controller.addToMethods(method).save()
			}
		}
	}

    /**
     * <p>Create a new user account, with domain and email notification etc deferred to the 
     * event handlers.</p>
     * <p>This method will first invoke the onFindByLogin event to see if a user already exists.
     * If so it will return a user object with the ERROR_LOGIN_NAME_NOT_AVAILABLE result.</p>
     * <p>If it does not already exist, it will then call the onNewUserObject event to create a new user
     * record in your backing store, which is typically a Grails domain class (or the default one supplied by
     * the plugin).</p>
     * <p>Next the onConfirmAccount event is triggered. This returns true to indicate that confirmation
     * of the user - i.e. two-phase registration, typicaly with email address confirmation - is needed. This
     * even must return false if no user confirmation is required.</p>
     * <p>After this, the onSaveUser event is called to save the new user details.</p>
     * <p>Finally, the onSignup event is called to indicate that a new account has been created, whether it
     * is pending confirmation or not. The "extraParams" object is passed to the event in case there are custom
     * fields to capture from the signup process that are opaque to the authentication plugin.</p>
     * @param params A map of parameters to include "login" and "password" at the very least, optionally
     * containing "email" (user's email address) and "immediate" (a boolean indicating whether the user should
     * be immediately logged in if confirmation is not being required) - and "extraParams" a map of any other parameters
     * typically the request params from the controller so that any custom fields from the signup process
     * can be captured and passed to the onSignup event.
     */
	@Transactional
	AuthenticatedUser signup(Map params) {
        assert !grailsApplication.config.authentication?.signup?.disabled, "Cannot perform signup, it is disabled in Config"
        
		def login = params.login
		def password = params.password
		
		assert login, "Method 'signup' requires a named login parameter"
		assert password, "Method 'signup' requires a named password parameter"
		
		def email = params.email
		def logInImmediately = params.immediate
		
		def user = fireEvent('FindByLogin', login)
		
		def authUser = new AuthenticatedUser(login:login)
		if (user) {
			authUser.result = AuthenticationResult.ERROR_LOGIN_NAME_NOT_AVAILABLE.id
			return authUser
		}
		
		user = fireEvent('NewUserObject', login)
		user.login = login
		user.password = encodePassword(password)
		user.email = email
		user.status = AuthenticationUserState.AWAITING_CONFIRMATION.id

		// See if confirmation required, onConfirmAccount will return true if so
		if (!params.suppressConfirmation && fireEvent('ConfirmAccount', user)) {
			logInImmediately = false // these are mutually exclusive settings
		} else {
			user.status = AuthenticationUserState.VALID.id
		}
		
		if (!fireEvent('SaveUser', user)) {
			log.error( "Failed to save user: $user / $login - ${user.errors}")
			throw new RuntimeException("Unable to save user details for ${login}")
		}

		// Store user id for quick lookups later
		authUser.userObjectId = user.id
		
		if (log.infoEnabled) {
			log.info("Sign up complete for user ${login}")
		}
		
		// Convert the status to a result code
		authUser.result = userStatusToResult(user.status)

        if ((authUser.result == 0) || (authUser.result == AuthenticationResult.AWAITING_CONFIRMATION.id)) {
            setSessionUser(authUser)
        } else {
            setSessionUser(null)
        }

		// Fire event
		fireEvent('Signup', [user:user, params:params.extraParams])

		if (logInImmediately) {
			doLoggedIn(authUser)
		}

		return authUser
	}
	@Transactional(readOnly = true)
	boolean exists(login) {
		return fireEvent('FindByLogin', login)
	}
	@Transactional
	AuthenticatedUser login(login, pass) {
		def user = fireEvent('FindByLogin', login)
		
		def token = new AuthenticatedUser(login:login, loginTime:new Date()) 

		if (!user) {
			token.result = AuthenticationResult.ERROR_NO_SUCH_LOGIN.id
            setSessionUser(null)		    
		}
		else if (user.password != encodePassword(pass)) {
			token.result = AuthenticationResult.ERROR_INCORRECT_CREDENTIALS.id
            setSessionUser(null)		    
		}
		else if( grailsApplication.config.enhanced.authentication.forbidMultipleSessions && user.sessionToken){
			token.result = AuthenticationResult.ALREADY_LOGGED.id
			setSessionUser(null)
		} 
		else {
			// Success
			updateSessionUser(user)
		}
		return token 
	}
	
	def updateSessionUser(user){
		def token = new AuthenticatedUser(login: user.login, loginTime: new Date()) 
		token.result = userStatusToResult(user.status)
		token.userObjectId = user.id
		token.attributes['userObjectId'] = user.id
		token.attributes['login'] = user.login
		token.attributes['roles'] = user.roles.collect{ it.name }
		token.attributes['firstName'] = user.firstName
		token.attributes['lastName'] = user.lastName
		token.attributes['email'] = user.email
		def sessionToken = fireEvent('CreateSessionToken',user.login)
		token.attributes['sessionToken'] = sessionToken
		setSessionUser(token)
		doLoggedIn(token)
	}
	
	protected userStatusToResult(def userStatus) {
		def value
		switch (userStatus) {
			case AuthenticationUserState.NEW.id : 
			case AuthenticationUserState.VALID.id :
				value = 0
				break
			case AuthenticationUserState.AWAITING_CONFIRMATION.id :
			case AuthenticationUserState.CONFIRMATION_LAPSED.id :
				value = AuthenticationUserState.AWAITING_CONFIRMATION.id
				break
			default :
				throw new IllegalArgumentException("Unrecognized value $userStatus passed to userStatusToResult")
		}
		return value
	}
	
	protected void doLoggedIn(AuthenticatedUser user) {
		user.loggedIn = true
		user.loginTime = new Date()
		if (log.infoEnabled) {
			log.info("Logged in user ${user.login}")
		}
		// Fire event
		fireEvent('LoggedIn', user)
	}
	@Transactional
	void logout(AuthenticatedUser authenticatedUser) {
		if (log.debugEnabled) {
			log.debug("Logging out with authenticated user object ${authenticatedUser}")
		}
		if(authenticatedUser){
			authenticatedUser.loggedIn = false
			def user = getUserPrincipal()
			user.sessionToken = null
			user.save(flush: true)
		}
		if (log.infoEnabled) {
			log.info("Logged out user ${authenticatedUser.login}")
		}
		fireEvent('LoggedOut', authenticatedUser)
	}
	
	/**
	 * Call when a user's account has been confirmed and can now be used
	 *
	 * @return true if user was found and confirmed, false if user not found
	 */
	@Transactional
	boolean confirmUser(login) {
		def user = fireEvent('FindByLogin', login)
	
		if (!user) {
			return false
		} else {
			user.status = AuthenticationUserState.VALID.id
			if (!fireEvent('SaveUser', user)){
				throw new RuntimeException("Unable to save confirmed user $user")
			}
            // Update the session to indicate user is logged in
			getSessionUser()?.result = userStatusToResult(user.status)
			return true
		}
	}

	protected void setSessionUser(AuthenticatedUser user) {
	    def attribs = RequestContextHolder.requestAttributes
	    if (attribs) {
	        attribs.request.session.setAttribute(SESSION_KEY_AUTH_USER, user)		
        }
	}

	AuthenticatedUser getSessionUser() {
	    def attribs = RequestContextHolder.requestAttributes
	    if (attribs) {
	        return attribs.request.session.getAttribute(SESSION_KEY_AUTH_USER)		
        }
		else{
			return processUser.get();
		}
	}
	
	def void setProcessUser(AuthenticationUser user){
		processUser.set(new AuthenticatedUser(userObjectId: user.id));
	}
	
	/** 
	 * Get the user domain object for the current logged in user. Caches it for the lifetime of the request
	 * so updates elsewhere may not be seen
	 */
	@Transactional(readOnly = true)
	def getUserPrincipal(boolean refresh = false) {
		
	    def req = RequestContextHolder.requestAttributes?.request
	    def loggedUserId = req?.getAt(REQUEST_KEY_AUTH_USER)
	    if (!loggedUserId || refresh) {
	        loggedUserId = getSessionUser()?.userObjectId
			// Cache it for lifetime of this request
			req?.putAt(REQUEST_KEY_AUTH_USER, loggedUserId)
        }
		def currentRequestUser = getUserDomainObjectById(loggedUserId)
	    return currentRequestUser
	}
	
	def resetUserPrincipal(){
		def req = RequestContextHolder.requestAttributes?.request
		req?.putAt(REQUEST_KEY_AUTH_USER, null)
		setSessionUser(null)
	}
	
	/**
	 * Get the instance of the user authentication object, no matter what application-supplied domain class is being used
	 */
	@Transactional(readOnly = true)
	def getUserDomainObjectById(id) {
	 
		   userDomainClass.get(id)
	}
	@Transactional
	boolean delete(login) {
		def user = fireEvent('FindByLogin', login)
		
		if (!user) {
			return false
		}
		
		// Fire event
		fireEvent('Delete', user)

		if (log.infoEnabled) {
			log.info("Deleted user ${login}")
		}
		
		return true
	}
	
	def handleAuthenticatedRequest = { request, loginUrl -> 
        if (!request.session.authenticatedUser?.loggedIn) {
            redirect(loginUrl)
            return false;
        } else {
            return true;
        }
	}
	
	/* 
	   Event handlers - stores all default event handler methods.
	   Default is a hash map with closure values for each property key but you can re-assign
	   eventHandler to any object within your app that also provides these properties.
	   
	   The events are functions or methods using the parameters and return values shown below.
	*/
    def events =  [
	    // Called to validate the user's chosen login name, i.e. is it too short or in use? Return true if valid
	    onValidateLogin:{ loginID -> true },
    	// Called to validate the user's password, i.e. is it long enough/strong enough. Return true if valid
    	onValidatePassword: { data -> new ValidationResult(valid: true) },
    	// Called to encode the user's password prior to saving it, i.e. return digested/XOR'd password
    	// Defaults to SHA1 hash, i.e. clear text in the database
    	onEncodePassword: { password -> password?.encodeAsSHA1() },
    	// Called to load the user object by login id, must retun the user object or null if not found
        onFindByLogin:{ loginID -> AuthenticationService.userDomainClass.findByLoginAndStatus(loginID, AuthenticationUserState.VALID.id) },
        // Called when a new user object is required, object returned must have login, password, email and status properties
        onNewUserObject: { loginID -> def obj = AuthenticationService.userDomainClass.newInstance(); obj.login = loginID; return obj },
        // Called when a user object has been changed and needs to be saved
        // This impl assumes its a GORM object
        onSaveUser: { user -> user.save() },
        // Called when a user has logged in
        onLoggedIn: { AuthenticatedUser login -> },
        // Called when a user has logged out
        onLoggedOut: { AuthenticatedUser login -> },
        // Called on successful signup, although email may not be confirmed yet - params are the request (form) params
        onSignup: { params -> },
        // Called when an account has to be deleted, passing in the user object supplied by onNewUserObject
        onDelete: { user -> user.delete() },
        // Called to see if email confirmation is required, return true if user cannot log in yet until confirmed
        onConfirmAccount: { user -> },
        // Called by requireAuthorization to see if a logged in user can access the requested resource
        // params has properties: requirement (the authorization object required), request, user, controllerName, actionName
        onHasAuthorization: { params -> true },
        // Called to see if a logged in user can access the requested resource
        // params has properties: request, user, controllerName, actionName
        onCheckAuthorized: { params ->
			if(params.controllerName != null && params.actionName!= null) {
				if(isSelfEditing(params)){
					true
				}
				else{
					hasPermissions(params.controllerName+":"+params.actionName)
				}
			}
			else{
				true
			}
		},        // Called after a denied access
        // params has properties: request, response
        onUnauthorizedAccess: { params -> params.response.sendError(403) },
		onCreateSessionToken: { login ->
			def sessionToken = generateAuthenticationToken(login)
			def user = fireEvent('FindByLogin', login)
			user.sessionToken = sessionToken
			user.save()
			sessionToken
		},
		onCheckSessionToken: { token -> 
			def user = AuthenticationUser.findBySessionToken(token)
			return user as Boolean
		},
		onPostTokenLogin: { login ->
			[:]
		},
		onBootstrapPermissions: {params ->},
		onBootstrapRoles: {params ->},
		onBootstrapUsers: {params ->}
    ]

    void configChanged() {
        log.info "Authentication reloading settings from config"
        // Take events from config if found
        def configObj = grailsApplication.config
        if (configObj.authenticationEvents) {
            events = configObj.authenticationEvents 
            log.info "Authentication loaded custom events from Config"
        }
        // Take domain class from config if found
        if (configObj.authenticationUserClass) {
            AuthenticationService.userDomainClass = configObj.authenticationUserClass 
            log.info "Authentication will use custom domain class from Config: ${AuthenticationService.userDomainClass}"
        }
    }
	
	/************** IMPORTANT **************/
	// These two methods have to live here
	// As we cannot put static methods in the service class itself
	// as they cannot then refer to an injected prefs service
	// The need to be static here to enable them to be used in domain class and command object
	// validators
	@Transactional(readOnly = true)
	def checkLogin(def value) {
		return fireEvent("ValidateLogin", value)
	}
	@Transactional(readOnly = true)
	def checkPassword(def value) {
		return fireEvent("ValidatePassword", value)
	}

	// This is here just so we can unit test easier, hard to mock out and assert a closure property
	def encodePassword(def value) {
		return fireEvent("EncodePassword", value)
	}

	// Trigger events
	def fireEvent(eventName, data) {
        if (log.debugEnabled) log.debug( "Firing event $eventName with $data")
		return events."on$eventName"(data)
	}	
	
	// Called to check it the current request has a successfully logged in user
	boolean isLoggedIn(request) {
	    def user = request.session.getAttribute(SESSION_KEY_AUTH_USER)
		return (user?.result == 0) && user?.loggedIn
	}
	@Transactional(readOnly = true)
	boolean hasRoles(roles) {
		def closure = {role, value -> role.name.equals(value)}
		return hasAccess(roles, closure)
	}
	@Transactional(readOnly = true)
	boolean hasPermissions(permissions) {
		def closure = {role, value -> role.permissions.find { it.validatePermission(value) } }
		return hasAccess(permissions, closure)
	}
	
	boolean isSelfEditing(params){
		if(params.controllerName == "authenticationUser"){
			def userId = params.user.userObjectId.toString()
			if (params.actionName == "edit" || params.actionName == "show"){
				def path=params.request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH)
				return path.endsWith("/" + userId)
			}
			else if ( params.actionName == "update") {
				def id = params.request.getParameter("id")
				return id.equals(userId)
			}
		}
		return false
	}
	@Transactional(readOnly = true)
	boolean hasId(ids){
		if(!Component.findAllByName(ids)){
			log.warn("Id ${ids} not registered")
			return false;
		}
		def closure = {role, value -> role.components.empty ? false : role.components.find { it.name.equals(value) } }
		def result= hasAccess(ids, closure)
		return result
	}
	@Transactional(readOnly = true)
	boolean hasAccess(values, closure){
		def user = getSessionUser()
		def valid = false
		if ((user?.result == 0) && user?.loggedIn){
			for(value in values.tokenize(',')) {
				value = value.trim()
				for(loggedUserRole in user.attributes.roles) {
					Role userRole=roleDomainClass.findByName(loggedUserRole)
					valid = closure(userRole, value)
					if(!valid){
						def roleCollector = { closure(it, value) ? it : (it.roles.isEmpty() ? null : it.roles.find(roleCollector)) }
						valid = userRole.roles.find(roleCollector) != null
					}
					if(valid){
						return valid
					}
				}
			}
		}
		return valid
	}
	
	
	/**
	 * Call from filters to enforce authenticated user, redirect to login form / use http auth,
	 * handle "next URL" mechanics and optionally enforce authorization
	 * loginURI must be URI relative to this server (must include application context) and will be 
	 * redirected to if the user is not logged in
	 */
	@Transactional(readOnly = true)
	boolean filterRequest( request, response, loginURI) {
        if (!isLoggedIn(request)) {
    	    if (log.debugEnabled) log.debug("Filtering request - user not logged in, redirecting to ${loginURI}")
            response.sendRedirect(loginURI) // Crappy but we can't use smart dynamic redirect
    	    return false // Indicate "don't carry on processing" as auth is required
        }
		
        // do authorization events
        if (fireEvent("CheckAuthorized", [request: request, user: request.session.getAttribute(SESSION_KEY_AUTH_USER),
                controllerName: request.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE), 
                actionName: request.getAttribute(GrailsApplicationAttributes.ACTION_NAME_ATTRIBUTE) ] )) {
    	    if (log.debugEnabled) log.debug("Filtering request - user ${request.session.getAttribute(SESSION_KEY_AUTH_USER)} authorized access")
            return true
        } else {
    	    if (log.debugEnabled) log.debug("Filtering request - user ${request.session.getAttribute(SESSION_KEY_AUTH_USER)} denied access")
            // Let the app know, for logging etc
            fireEvent("UnauthorizedAccess", [request:request, response:response])
            return false
        } 
	}

	/**
	 * Call from filters to enforce authenticated user, redirect to login form / use http auth,
	 * handle "next URL" mechanics and optionally enforce authorisation
	 * loginURI must be URI relative to this server (must include application context) and will be 
	 * redirected to if the user is not logged in
	 * This variant gets the req/resp from the threadlocal context - slower than if you already have them, but better
	 * if you don't / want less wordy code
	 */
	boolean filterRequest( loginURI) {
        filterRequest( RequestContextHolder.requestAttributes.request, 
            RequestContextHolder.requestAttributes.currentResponse, 
            loginURI)
	}
	
	/**
	 * Call from filters to enforce authenticated user, redirect to login form / use http auth,
	 * handle "next URL" mechanics and optionally enforce authorisation 
	 * AND calls into events to check the user has the desired "requirement" - which is any object you like
	 * This is used to implement per-controller/action role/permission enforcement using Filters
	 * loginURI must be URI relative to this server (must include application context) and will be 
	 * redirected to if the user is not logged in
	 */
	@Transactional(readOnly = true)
	boolean requireAuthorization(requirement, loginURI) {
	    def request = RequestContextHolder.requestAttributes.request
	    def response = RequestContextHolder.requestAttributes.currentResponse
        if (!isLoggedIn(request)) {
    	    if (log.debugEnabled) log.debug("Authorizing request - user not logged in, redirecting to ${loginURI}")
            response.sendRedirect(loginURI) // Crappy but we can't use smart dynamic redirect
    	    return false // Indicate "don't carry on processing" as auth is required
        }

        // do authorisation events 
        if (fireEvent("HasAuthorization", [requirement:requirement, request: request, user: request.session.getAttribute(SESSION_KEY_AUTH_USER),
                controllerName: request.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE), 
                actionName: request.getAttribute(GrailsApplicationAttributes.ACTION_NAME_ATTRIBUTE) ] )) {
    	    if (log.debugEnabled) log.debug("Authorizing request - user ${request.session.getAttribute(SESSION_KEY_AUTH_USER)} authorized access")
            return true
        } else {
    	    if (log.debugEnabled) log.debug("Authorizing request - user ${request.session.getAttribute(SESSION_KEY_AUTH_USER)} denied access")
            // Let the app know, for logging etc
            fireEvent("UnauthorizedAccess", [request:request, response:response])
            return false
        } 
	}
	
	protected generateAuthenticationToken(login){
		encodePassword(login + new Date().time)
	}
	
	@Transactional
	def generatePasswordResetLink(user){
		def token = generateAuthenticationToken(user.login)
		user.passwordResetToken = token
		int timeout = grailsApplication.config.enhanced?.authentication?.passwordResetTimeout ? grailsApplication.config.enhanced?.authentication?.passwordResetTimeout : PASSWORD_RESET_DEFAULT_TIMEOUT
		Calendar currentTime =  Calendar.getInstance()
		currentTime.add(Calendar.MINUTE, timeout)
		user.passwordResetTimeout = currentTime.getTime()
		user.password = token
		user.save(flush: true)
		"/authentication/resetPassword/${token}"
	}
	@Transactional(readOnly = true)
	def validatePasswordResetLink(token){
		AuthenticationUser.findByPasswordResetTokenAndStatusNotEqualAndPasswordResetTimeoutGreaterThan(token, AuthenticationUserState.DEACTIVATED.id, new Date())
	}
}

