// Place your Spring DSL code here
beans = {
	sessionTokenService(com.hexacta.enhanced.authentication.SessionTokenService) { bean ->
		bean.scope = 'session'
		authenticationService = ref("authenticationService")
		bean.destroyMethod = 'destroy'
    }
}