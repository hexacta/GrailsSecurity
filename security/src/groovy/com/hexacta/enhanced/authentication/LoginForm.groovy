package com.hexacta.enhanced.authentication

import grails.validation.Validateable

@Validateable
class LoginForm implements Serializable {
	String login
	String password
	
	boolean rememberMe
	
	static constraints = {
		login(size:1..100, nullable: false, blank:false)
		password(size:1..100, password:true, nullable: false, blank:false)
	}
}
