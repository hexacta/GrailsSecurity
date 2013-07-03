package com.hexacta.enhanced.authentication;

public enum AuthenticationUserState {
	NEW(0), VALID(1), AWAITING_CONFIRMATION(2), CONFIRMATION_LAPSED(3), DEACTIVATED(4);
	
	public final int id;
	
	private AuthenticationUserState(int id){
		this.id = id;
	}
}
