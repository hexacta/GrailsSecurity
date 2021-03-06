import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.Environment;
import com.hexacta.enhanced.authentication.Permission;
import com.hexacta.enhanced.authentication.Role;
import com.hexacta.enhanced.authentication.AuthenticationUser;
import com.hexacta.enhanced.authentication.ValidationResult;

class BootStrap {
	def authenticationService
	def dataSource
	def grailsApplication
	
    def init = { servletContext ->
		registerAuthenticationBootstrap()
		authenticationService.bootstrap()
    }
	
	
	def registerAuthenticationBootstrap() {
		//authenticationService.tokenAuthenticatedActions << [controller: 'wsTest', action: '*']
		authenticationService.events.onBootstrapRoles = { params ->
			def admin = getRole('Admin')
			admin.addToPermissions(authenticationService.getPermission(Permission.ALL, Permission.ALL)).save()
		}
		authenticationService.events.onBootstrapUsers = { params ->
			def admin = AuthenticationUser.findByLogin('admin')
			if(!admin){
				admin = new AuthenticationUser(login: 'admin', firstName: 'admin', lastName: 'admin', email: 'admin@localhost.com', status: 1)
				admin.password = 'admin'.encodeAsSHA1()
				admin.addToRoles(Role.findByName('Admin'))
				admin.save()
			}
		}
	}
	
	def getRole(name){
		def role = Role.findByName(name)
		if(!role){
			role = new Role(name: name);
		}
		return role
	}
	
    def destroy = {
    }
}
