package com.hexacta.enhanced.authentication

import org.springframework.dao.DataIntegrityViolationException

import com.hexacta.enhanced.authentication.AuthenticationService
import com.hexacta.enhanced.authentication.AuthenticationUser

import com.hexacta.enhanced.authentication.annotations.Visible

@Visible(key="authenticationUser")
class AuthenticationUserController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	def authenticationService

    def index() {
        redirect(action: "list", params: params)
    }
	
	@Visible(key="list")
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [authenticationUserInstanceList: AuthenticationUser.list(params), authenticationUserInstanceTotal: AuthenticationUser.count()]
    }
	
	@Visible(key="create")
    def create() {
        [authenticationUserInstance: new AuthenticationUser(params)]
    }

	@Visible(key="save")
    def save() {
        def authenticationUserInstance = new AuthenticationUser(params)
		authenticationUserInstance.status = AuthenticationUserState.VALID.id
		def validationResult = authenticationService.checkPassword([user: authenticationUserInstance, newPassword: authenticationUserInstance.password]) 
		if(!validationResult.valid){
			authenticationUserInstance.errors.rejectValue("password", message(code: validationResult.messageKey, args: validationResult.parameters))
			render(view: "create", model: [authenticationUserInstance: authenticationUserInstance])
			return
		}
		authenticationUserInstance.password = authenticationService.encodePassword(authenticationUserInstance.password)
        if (!authenticationUserInstance.save(flush: true)) {
            render(view: "create", model: [authenticationUserInstance: authenticationUserInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), authenticationUserInstance.id])
        redirect(action: "show", id: authenticationUserInstance.id)
    }

	@Visible(key="show")
    def show(Long id) {
        def authenticationUserInstance = AuthenticationUser.get(id)
        if (!authenticationUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
            redirect(action: "list")
            return
        }

        [authenticationUserInstance: authenticationUserInstance]
    }

	@Visible(key="edit")
    def edit(Long id) {
        def authenticationUserInstance = AuthenticationUser.get(id)
        if (!authenticationUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
            redirect(action: "list")
            return
        }
		authenticationUserInstance.password = null
        [authenticationUserInstance: authenticationUserInstance]
    }

	@Visible(key="update")
    def update(Long id, Long version) {
        def authenticationUserInstance = AuthenticationUser.get(id)
		if(authenticationUserInstance && !authenticationService.checkPassword(authenticationUserInstance.password)){
			authenticationUserInstance.errors.rejectValue("password", "authentication.invalidPassword")
			render(view: "create", model: [authenticationUserInstance: authenticationUserInstance])
			return
		}
        if (!authenticationUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (authenticationUserInstance.version > version) {
                authenticationUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'authenticationUser.label', default: 'AuthenticationUser')] as Object[],
                          "Another user has updated this AuthenticationUser while you were editing")
                render(view: "edit", model: [authenticationUserInstance: authenticationUserInstance])
                return
            }
        }

        authenticationUserInstance.properties = params
		authenticationUserInstance.status = AuthenticationUserState.VALID.id
		if(params.password){
			authenticationUserInstance.password = authenticationService.encodePassword(params.password)
		}

        if (!authenticationUserInstance.save(flush: true)) {
            render(view: "edit", model: [authenticationUserInstance: authenticationUserInstance])
            return
        }
		// Re login the user if it's modifying himself 
		if(authenticationService.getUserPrincipal()?.id == authenticationUserInstance?.id){
			log.info("relogin")
			authenticationService.updateSessionUser(authenticationUserInstance)
		}
        flash.message = message(code: 'default.updated.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), authenticationUserInstance.id])
        redirect(action: "show", id: authenticationUserInstance.id)
    }

	@Visible(key="delete")
    def delete(Long id) {
        def authenticationUserInstance = AuthenticationUser.get(id)
        if (!authenticationUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
            redirect(action: "list")
            return
        }
		if(!authenticationUserInstance.allowDeletion){
			flash.message = message(code: 'authentication.deleteDisabled')
			redirect(action: "list")
			return
		}

        try {
            authenticationUserInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
            redirect(action: "show", id: id)
        }
    }
	
	@Visible(key="resetPassword")
	def resetPassword(){
		def authenticationUserInstance = AuthenticationUser.get(params.id)
		if (!authenticationUserInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'authenticationUser.label', default: 'AuthenticationUser'), id])
			redirect(action: "list")
			return
		}
		def link = authenticationService.generatePasswordResetLink(authenticationUserInstance)
		def url = request.scheme + '://' + request.serverName + ':'+ request.serverPort + request.contextPath + link
		render(view: "show", model: [authenticationUserInstance: authenticationUserInstance, passwordResetLink: url])
	}
}
