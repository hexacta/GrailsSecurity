package com.hexacta.enhanced.authentication

import grails.test.mixin.*
import org.junit.*

import com.hexacta.enhanced.authentication.Role;

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Role)
class RoleTests {

    void testToString() {
       def name = 'name'
	   def role = new Role()
	   role.name = name
	   assert role.toString() == name
    }
}
