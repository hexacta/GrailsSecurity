package com.hexacta.enhanced.authentication

import grails.test.mixin.*
import org.junit.*

import com.hexacta.enhanced.authentication.ControllerConfiguration;

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(ControllerConfiguration)
class ControllerTests {

    void testSomething() {
       ControllerConfiguration cc = new ControllerConfiguration(label: 'label')
	   assert 'label' == cc.toString()
    }
}
