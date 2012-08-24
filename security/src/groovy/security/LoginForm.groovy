package security

import grails.validation.Validateable

@Validateable
class LoginForm implements Serializable {
	String login
	String password
	
	boolean rememberMe
	
	static constraints = {
		login(size:5..40, nullable: false, blank:false)
		password(size:5..40, password:true, nullable: false, blank:false)
	}
}
