package com.hexacta.enhanced.authentication

import org.junit.*

import com.hexacta.enhanced.authentication.AuthenticationService;
import com.hexacta.enhanced.authentication.AuthenticationUser;
import com.hexacta.enhanced.authentication.AuthenticationUserController;
import com.hexacta.enhanced.authentication.Role;

import grails.test.mixin.*

@TestFor(AuthenticationUserController)
@Mock([AuthenticationUser, Role, AuthenticationService])
class AuthenticationUserControllerTests {

    def populateValidParams(params) {
        assert params != null
		params["login"] = 'login'
		params["password"] = 'password'
		params["email"] = 'name@email.com'
		params["firstName"] = 'firstname'
		params["lastName"] = 'lastname'
		params["role.id"] = '1'
    }

    void testIndex() {
        controller.index()
        assert "/authenticationUser/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.authenticationUserInstanceList.size() == 0
        assert model.authenticationUserInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.authenticationUserInstance != null
    }

    void testSave() {
        controller.save()

        assert model.authenticationUserInstance != null
        assert view == '/authenticationUser/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/authenticationUser/show/1'
        assert controller.flash.message != null
        assert AuthenticationUser.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/authenticationUser/list'

        populateValidParams(params)
        def authenticationUser = new AuthenticationUser(params)

        assert authenticationUser.save() != null

        params.id = authenticationUser.id

        def model = controller.show()

        assert model.authenticationUserInstance == authenticationUser
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/authenticationUser/list'

        populateValidParams(params)
        def authenticationUser = new AuthenticationUser(params)

        assert authenticationUser.save() != null

        params.id = authenticationUser.id

        def model = controller.edit()

        assert model.authenticationUserInstance == authenticationUser
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/authenticationUser/list'

        response.reset()

        populateValidParams(params)
        def authenticationUser = new AuthenticationUser(params)

        assert authenticationUser.save() != null
        assert AuthenticationUser.count() == 1

        params.id = authenticationUser.id

        controller.delete()

        assert AuthenticationUser.count() == 0
        assert AuthenticationUser.get(authenticationUser.id) == null
        assert response.redirectedUrl == '/authenticationUser/list'
    }
}
