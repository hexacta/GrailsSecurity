package security

import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Method)
class MethodTests {

    void testSomething() {
       Method m = new Method(label: "label")
	   assert "label" == m.toString()
    }
}
