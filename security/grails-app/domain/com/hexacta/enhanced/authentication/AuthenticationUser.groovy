package com.hexacta.enhanced.authentication

class AuthenticationUser { 
	String login
	String firstName
	String lastName
	String password
	String email
	int status = AuthenticationUserState.NEW.id
	Role role
	String sessionToken
	String passwordResetToken
	Date passwordResetTimeout
	boolean allowDeletion = true

	static constraints = {
		login(size:1..100, unique: true)
		firstName(size:1..100)
		lastName(size:1..100)
		password(size:1..100, password: true)
		email(email:true, nullable: true, blank: false)
		status(inList:[
			AuthenticationUserState.NEW.id, 
			AuthenticationUserState.VALID.id, 
			AuthenticationUserState.AWAITING_CONFIRMATION.id, 
			AuthenticationUserState.CONFIRMATION_LAPSED.id,
			AuthenticationUserState.DEACTIVATED.id
		])
		sessionToken(nullable: true)
		passwordResetToken(nullable: true)
		passwordResetTimeout(nullable: true)
	}
	
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'USER'
	}
	
	@Override
	public String toString() {
		return firstName+" "+lastName;
	}
}	
