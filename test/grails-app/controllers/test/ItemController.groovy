package test

import org.springframework.dao.DataIntegrityViolationException
import com.hexacta.enhanced.authentication.annotations.Visible

@Visible(key="item")
class ItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def authenticationService
	
    def index() {
        redirect(action: "list", params: params)
    }

	@Visible(key="list")
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [itemInstanceList: Item.list(params), itemInstanceTotal: Item.count()]
    }

	@Visible(key="create")
    def create() {
		if("testuser" == authenticationService.getUserPrincipal().login){
			params.name = "Test Item"
		}
		if(authenticationService.hasRoles("User")){
			params.price = 15.0f
		}
		if(authenticationService.hasPermissions("item:list")){
			params.quantity = 3
		}
        [itemInstance: new Item(params)]
    }
	
	def internalMethod(){
		//Don't want to expose it
	}

    def save() {
        def itemInstance = new Item(params)
        if (!itemInstance.save(flush: true)) {
            render(view: "create", model: [itemInstance: itemInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'item.label', default: 'Item'), itemInstance.id])
        redirect(action: "show", id: itemInstance.id)
    }

    def show(Long id) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        [itemInstance: itemInstance]
    }

    def edit(Long id) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        [itemInstance: itemInstance]
    }

    def update(Long id, Long version) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (itemInstance.version > version) {
                itemInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'item.label', default: 'Item')] as Object[],
                          "Another user has updated this Item while you were editing")
                render(view: "edit", model: [itemInstance: itemInstance])
                return
            }
        }

        itemInstance.properties = params

        if (!itemInstance.save(flush: true)) {
            render(view: "edit", model: [itemInstance: itemInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'item.label', default: 'Item'), itemInstance.id])
        redirect(action: "show", id: itemInstance.id)
    }

    def delete(Long id) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        try {
            itemInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def resetLink(){
		def user = authenticationService.getUserPrincipal()
		def url = request.scheme + '://' + request.serverName + ':'+ request.serverPort + request.contextPath + authenticationService.generatePasswordResetLink(user)
		render url
	}
}
