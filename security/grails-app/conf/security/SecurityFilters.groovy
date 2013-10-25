package security

class SecurityFilters {
	def authenticationService
	def disabled
	def grailsApplication
	
	def checkDisabled(){
		if(disabled == null){
			disabled = grailsApplication.config?.enhanced?.authentication?.disable?.filter
		}
		disabled
	}
	
	def filters = { 
		accessFilter(controller:'*', action:'*') { 
			before = {
				if(checkDisabled()){
					return true 
				}
				def authenticationType = authenticationService.getAuthenticationType(controllerName, actionName)
				return filterStrategy[authenticationType](params, request, response)
			} 
		} 		
	}
	
	def filterStrategy = [
		byCredentials: { params, request, response ->
			authenticationService.filterRequest( request, response, "${request.contextPath}/authentication/index")
		},
		byToken: { params, request, response ->
			def validToken
			if(!params.token){
				authenticationService.fireEvent('UnauthorizedAccess',[response: response])
				validToken = false
			}
			else{
				validToken = authenticationService.fireEvent('CheckSessionToken', params.token)
			}
			if(!validToken){
				authenticationService.fireEvent('UnauthorizedAccess',[response: response])
			}
			validToken
		},	
		noFiltering: { params, request, response ->	true}
	]
}
