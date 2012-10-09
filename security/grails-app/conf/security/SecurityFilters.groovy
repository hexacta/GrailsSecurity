package security

class SecurityFilters {
	def authenticationService
	
	def filters = { 
		accessFilter(controller:'*', action:'*') { 
			before = {
				boolean needsAuth = authenticationService.needsAuthentication(controllerName, actionName)
				if (needsAuth) {
					return authenticationService.filterRequest( request, response, "${request.contextPath}/authentication/index") 
				} 
				return true
			} 
		} 
	}
}
