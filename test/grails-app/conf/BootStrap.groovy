import grails.util.Environment
import security.AuthenticationUser
import security.Role
import security.Permission

class BootStrap {
	def authenticationService

	def init = { servletContext ->
	  if(Environment.DEVELOPMENT == Environment.current){
		  authenticationService.events.onBootstrapRoles = { params ->
			  def admin = new Role(name: 'Admin').save()
			  admin.addToPermissions(authenticationService.getPermission(Permission.ALL, Permission.ALL)).save()
			  def userRole = new Role(name: 'User').save()
		  }
		  authenticationService.events.onBootstrapUsers = { params ->
			  def admin = new AuthenticationUser(login: 'admin', firstName: 'admin', lastName: 'admin', email: 'admin@localhost.com', status: 1)
			  admin.password = 'admin'.encodeAsSHA1()
			  admin.role = Role.findByName('Admin')
			  admin.save()
			  def testUser = new AuthenticationUser(login: 'testuser', firstName: 'testuser', lastName: 'testuser', email: 'testuser@localhost.com', status: 1)
			  testUser.password = 'testuser'.encodeAsSHA1()
			  testUser.role = Role.findByName('User')
			  testUser.save()
		  }
  
	  }
	  authenticationService.bootstrap()
	}
	
    def destroy = {
    }
}
