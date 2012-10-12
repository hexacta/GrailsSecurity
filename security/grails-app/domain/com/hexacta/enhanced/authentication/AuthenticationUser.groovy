package com.hexacta.enhanced.authentication

import com.hexacta.enhanced.authentication.AuthenticationService;

class AuthenticationUser { 
	String login
	String firstName
	String lastName
	String password
	String email
	int status = AuthenticationService.STATUS_NEW
	Role role

	static constraints = {
		login(size:5..64, unique: true)
		firstName(size:5..64)
		lastName(size:5..64)
		password(size:5..64, password: true)
		email(email:true, nullable: true, blank: false)
		status(inList:[
			AuthenticationService.STATUS_NEW, 
			AuthenticationService.STATUS_VALID, 
			AuthenticationService.STATUS_AWAITING_CONFIRMATION, 
			AuthenticationService.STATUS_CONFIRMATION_LAPSED
		])
	}
	
	@Override
	public String toString() {
		return firstName+" "+lastName;
	}
}	
