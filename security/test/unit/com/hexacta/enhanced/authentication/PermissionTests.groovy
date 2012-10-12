package com.hexacta.enhanced.authentication

import grails.test.mixin.*
import org.junit.*

import com.hexacta.enhanced.authentication.Permission;

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Permission)
class PermissionTests {

    void testValidateController() {
		def permission = new Permission(controller: new ControllerConfiguration(name:"controller"), method: new Method(name: "method"))
		assert permission.validateController("controller")
		assert !permission.validateController("controller2")
		permission = new Permission(controller: new ControllerConfiguration(name:Permission.ALL), method: new Method(name: "method"))
		assert permission.validateController("controller")
    }
	
    void testValidateMethod() {
		def permission = new Permission(controller: new ControllerConfiguration(name:"controller"), method: new Method(name: "list"))
		assert permission.validateMethod("list")
		assert !permission.validateMethod("show")
		permission = new Permission(controller: new ControllerConfiguration(name:"controller"), method: new Method(name: Permission.ALL))
		assert permission.validateMethod("list")
    }
	
    void testValidatePermission() {
		def permission = new Permission(controller: new ControllerConfiguration(name:"controller"), method: new Method(name: "list"))
		assert permission.validatePermission("controller:list")
		assert !permission.validatePermission("controller:show")
		permission = new Permission(controller: new ControllerConfiguration(name:"controller"), method: new Method(name: Permission.ALL))
		assert permission.validatePermission("controller:"+"list")
		permission = new Permission(controller: new ControllerConfiguration(name:Permission.ALL), method: new Method(name: "list"))
		assert permission.validatePermission("controller:"+"list")
		permission = new Permission(controller: new ControllerConfiguration(name:Permission.ALL), method: new Method(name: Permission.ALL))
		assert permission.validatePermission("controller:"+"list")
    }
}
