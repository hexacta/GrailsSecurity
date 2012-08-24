package security

import grails.test.mixin.*
import org.junit.*

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
