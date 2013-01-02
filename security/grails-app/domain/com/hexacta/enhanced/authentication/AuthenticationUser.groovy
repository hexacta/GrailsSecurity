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
	String sessionToken
	String passwordResetToken
	Date passwordResetTimeout

	static constraints = {
		login(size:1..100, unique: true)
		firstName(size:1..100)
		lastName(size:1..100)
		password(size:1..100, password: true)
		email(email:true, nullable: true, blank: false)
		status(inList:[
			AuthenticationService.STATUS_NEW, 
			AuthenticationService.STATUS_VALID, 
			AuthenticationService.STATUS_AWAITING_CONFIRMATION, 
			AuthenticationService.STATUS_CONFIRMATION_LAPSED,
			AuthenticationService.STATUS_DEACTIVATED
		])
		sessionToken(nullable: true)
		passwordResetToken(nullable: true)
		passwordResetTimeout(nullable: true)
	}
	
	@Override
	public String toString() {
		return firstName+" "+lastName;
	}
}	
