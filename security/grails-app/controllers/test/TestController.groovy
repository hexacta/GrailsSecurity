package test

class TestController {
	def authenticationService
    def getLink() {
		def user = authenticationService.getUserPrincipal()
		def url = request.scheme + '://' + request.serverName + ':'+ request.serverPort + request.contextPath + authenticationService.generatePasswordResetLink(user)
		render(view: "test", model: [resetLink: url])
	}
}
