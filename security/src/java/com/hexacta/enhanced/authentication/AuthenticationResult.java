package com.hexacta.enhanced.authentication;

public enum AuthenticationResult {
	ERROR_NO_SUCH_LOGIN(1), ERROR_INCORRECT_CREDENTIALS(2), ERROR_LOGIN_NAME_NOT_AVAILABLE(3), AWAITING_CONFIRMATION(4), ALREADY_LOGGED(5);
	
	public final int id;
	
	private AuthenticationResult(int id){
		this.id = id;
	}
}
