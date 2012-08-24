package security

class SecurityFilters {
	static nonAuthenticatedActions = [
		[controller:'authentication', action:'*']
	]
	
	def authenticationService

	def filters = { 
		accessFilter(controller:'*', action:'*') { 
			before = { 
				boolean needsAuth = !nonAuthenticatedActions.find { 
					(it.controller == controllerName) && ((it.action == '*') || (it.action == actionName)) 
				} 
				if (needsAuth) { 
					return applicationContext.authenticationService.filterRequest( request, response, "${request.contextPath}/authentication/index" ) 
				} 
				return true
			} 
		} 
	}
}
