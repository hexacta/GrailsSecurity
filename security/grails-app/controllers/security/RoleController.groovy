package security

import org.springframework.dao.DataIntegrityViolationException
import security.annotations.Visible

@Visible(key="role")
class RoleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	def authenticationService
	
    def index() {
        redirect(action: "list", params: params)
    }

	@Visible(key="list")
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [roleInstanceList: Role.list(params), roleInstanceTotal: Role.count()]
    }

	@Visible(key="create")
    def create() {
		params.name = ''
		params.permissions = []
		loadControllers()
        [roleInstance: new Role(params)]
    }
	
	def loadControllers(){
		def controllers = grailsApplication.getArtefacts("Controller").collect { it.getLogicalPropertyName()}
		controllers.add("*")
		session.controllers = controllers.sort {it}
	}

	@Visible(key="save")
    def save() {
        def roleInstance = new Role(params)
		if (!roleInstance.save(flush: true)) {
            render(view: "create", model: [roleInstance: roleInstance])
            return
        }
        flash.message = message(code: 'default.created.message', args: [message(code: 'role.label', default: 'Role'), roleInstance.id])
        redirect(action: "show", id: roleInstance.id)
    }

	@Visible(key="show")
    def show(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "list")
            return
        }

        [roleInstance: roleInstance]
    }

	@Visible(key="edit")
    def edit(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "list")
            return
        }
		loadControllers()
        [roleInstance: roleInstance]
    }
	
	def updateMethods(){
		def responseText = "<select name=\"permissionMethod\">"
		ControllerConfiguration.findByName(params.selectedController).methods.sort { it.id }.each {
			responseText += "<option value=\"" + it.name + "\">" + message(code: "method." + it.label) + "</option>"
		}
		responseText += "</select>"
		render responseText
	}
	
	def addPermission() {
        def roleInstance = new Role(params)
		def permission = authenticationService.getPermission(params.permissionController, params.permissionMethod)
		roleInstance.addToPermissions(permission)
		render(template: "permissionsTable", model: [roleInstance: roleInstance])
	}
	
	def removePermission() {
		def roleInstance = new Role(params)
		def permission = Permission.get(params.id)
		roleInstance.removeFromPermissions(permission)
		render(template: "permissionsTable", model: [roleInstance: roleInstance])
	}
	
	def addComponent() {
		def roleInstance = new Role(params)
		def component = Component.findByName(params.componentName)
		if(component){
			roleInstance.addToComponents(component)
		}
		else{
			flash.message = message(code: 'default.component.not.found.message', args: [params.componentName])
		}
		render(template: "componentsTable", model: [roleInstance: roleInstance])
	}
	
	def removeComponent() {
		def roleInstance = new Role(params)
		def component = Component.get(params.id)
		roleInstance.removeFromComponents(component)
		render(template: "componentsTable", model: [roleInstance: roleInstance])
	}

	@Visible(key="update")
    def update(Long id, Long version) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (roleInstance.version > version) {
                roleInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'role.label', default: 'Role')] as Object[],
                          "Another user has updated this Role while you were editing")
                render(view: "edit", model: [roleInstance: roleInstance])
                return
            }
        }

        roleInstance.properties = params

		if (!roleInstance.save(flush: true)) {
            render(view: "edit", model: [roleInstance: roleInstance])
            return
        }
		session.permissionList = null
        flash.message = message(code: 'default.updated.message', args: [message(code: 'role.label', default: 'Role'), roleInstance.id])
        redirect(action: "show", id: roleInstance.id)
    }

	@Visible(key="delete")
    def delete(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "list")
            return
        }

        try {
            roleInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "show", id: id)
        }
    }
}
