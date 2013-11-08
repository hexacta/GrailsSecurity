package com.hexacta.enhanced.authentication

import grails.converters.JSON;
import grails.util.Environment

import com.hexacta.enhanced.authentication.AuthenticatedUser
import com.hexacta.enhanced.authentication.LoginForm
import com.hexacta.enhanced.authentication.SignupForm

class AuthenticationController {
	def defaultAction = "index"
	
	def authenticationService
	
	def sessionScopeProxy

    /**
     * Extract success_* and error_* into maps that can be passed to redirect(),
     * but forbidding the use of "url" which could lead to XSS attacks or phishing
     */
    def extractParams() {
        def redirectParams = [success:[:], error:[:]]
        params.keySet().each() { name -> 
            if (name.startsWith("success_") || name.startsWith('error_')) {
                def underscore = name.indexOf('_')
                if (underscore >= name.size()-1) return
                def prefix = name[0..underscore-1]
                def urlParam = name[underscore+1..-1]
                if (urlParam != 'url') {
                    redirectParams[prefix][urlParam] = params[name]
                }
            }
        }
        return redirectParams
    }

    def index = {
		if(Environment.DEVELOPMENT != Environment.current) {
			log.warn("Using provided login")
		}
		if(grailsApplication.config.enhanced?.authentication?.loginRedirect){
			def loginRedirect = grailsApplication.config.enhanced?.authentication?.loginRedirect
			authenticationService.nonAuthenticatedActions += loginRedirect
			redirect(loginRedirect)
		}
    }
    
	def login = { LoginForm form ->
	    def urls = extractParams()
		if (!form.hasErrors()) {
			session.authenticationMessage = null 
			def loginResult = authenticationService.login( form.login, form.password)
			if (loginResult.result == 0) {
				flash.loginForm = form
				sessionScopeProxy.init()
				if (log.debugEnabled) log.debug("Login succeeded for [${form.login}]")
				redirect(flash.authSuccessURL ? flash.authSuccessURL : urls.success)
			} else {                  
				flash.loginForm = form
				flash.authenticationFailure = loginResult
				session.authenticationMessage= message(code: 'authentication.logIn.Failed')
				if (log.debugEnabled) log.debug("Login failed for [${form.login}] - reason: ${loginResult.result}")
				redirect(flash.authFailureURL ? flash.authFailureURL : urls.error)
			}
		} else {
			flash.loginForm = form
			flash.loginFormErrors = form.errors // Workaround for grails bug 
			if (log.debugEnabled) log.debug("Login failed for [${form.login}] - form invalid: ${form.errors}")
			redirect(flash.authErrorURL ? flash.authErrorURL : urls.error)
		}
	}

	def signup = { SignupForm form ->
	    def urls = extractParams()
		if (!form.hasErrors()) {
			def signupResult = authenticationService.signup( login:form.login, 
				password:form.password, email:form.email, immediate:true, extraParams:params)
			if ((signupResult.result == 0) || (signupResult.result == AuthenticatedUser.AWAITING_CONFIRMATION)) {
				if (log.debugEnabled) {
					if (signupResult == AuthenticatedUser.AWAITING_CONFIRMATION) {
						log.debug("Signup succeeded pending email confirmation for [${form.login}] / [${form.email}]")
					} else {
						log.debug("Signup succeeded for [${form.login}]")
					}
				}
				redirect(flash.authSuccessURL ? flash.authSuccessURL : urls.success)
			} else {
				flash.authenticationFailure = signupResult
				flash.signupForm = form
				if (log.debugEnabled) log.debug("Signup failed for [${form.login}] reason ${signupResult.result}")
				redirect(flash.authErrorURL ? flash.authErrorURL : urls.error)
			}
		} else {
			flash.signupForm = form
			flash.signupFormErrors = form.errors // Workaround for grails bug in 0.5.6
			if (log.debugEnabled) log.debug("Signup failed for [${form.login}] - form invalid: ${form.errors}")
			redirect(flash.authErrorURL ? flash.authErrorURL : urls.error)
		}		
	}

	def logout = { 
	    def urls = extractParams()
		authenticationService.logout( authenticationService.sessionUser )
		redirect(flash.authSuccessURL ? flash.authSuccessURL : urls.success)
	}
	
	def resetPassword(){
		authenticationService.resetUserPrincipal()
		def user = authenticationService.validatePasswordResetLink(params.id)
		if(!user){
			// Invalid link
			def expiredLinkRedirect = grailsApplication.config.enhanced?.authentication?.expiredLinkRedirect
			log.info("Password reset link expired, redirecting to ${expiredLinkRedirect}")
			if(expiredLinkRedirect){
				// Custom redirect
				redirect(expiredLinkRedirect)
			}
			else{
				// Default redirect
				authenticationService.fireEvent('unauthorizedAccess')
			}
		}
		else{
			// Valid link
			def passwordRedirect = grailsApplication.config.enhanced?.authentication?.passwordResetRedirect
			if(passwordRedirect){
				//Custom password reset page
				passwordRedirect.id = params.id
				redirect(passwordRedirect)
			}
			else{
				// Default password reset page
				[authenticationUserInstance: user]
			}
		}
	}
	
	def changePassword(){
		def user = authenticationService.fireEvent('FindByLogin',params.login)
		if(params.newPassword != params.newPasswordConfirmation){
			flash.message = message(code: "authentication.passwordsDontMatch")
			render(view: "resetPassword", model: [authenticationUserInstance: user])
			return
		}
		def validationResult = authenticationService.checkPassword([user: user, newPassword: params.newPassword])
		if(!validationResult?.valid){
			flash.message = message(code: validationResult.messageKey, args: validationResult.parameters)
			render(view: "resetPassword", model: [authenticationUserInstance: user])
			return
		}
		user.password = authenticationService.encodePassword(params.newPassword)
		user.passwordResetToken = null
		user.passwordResetTimeout = null
		user.save(flush: true)
		flash.message = message(code: 'authentication.passwordUpdated')
		render(view: "resetPassword", model: [authenticationUserInstance: user])
	}
	
	def wsLogin(){
		def login = params.user
		def password = params.password // Hashed as SHA1
		def user = authenticationService.fireEvent('FindByLogin', login)
		if(!user || user.password != password){
			authenticationService.fireEvent('UnauthorizedAccess', [response: response])
		}
		else {
			def token = user.sessionToken
			if(!token){ 
				token = authenticationService.fireEvent('CreateSessionToken', login)
			}
			def result = authenticationService.fireEvent('PostTokenLogin', login)
			result.token = token
			render result as JSON
		}
	}
	
	def mainPage = {
		redirect(uri: '/')	
	}
}

