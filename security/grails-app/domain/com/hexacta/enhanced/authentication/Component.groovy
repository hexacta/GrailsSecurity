package com.hexacta.enhanced.authentication

class Component {
	String name
	static belongsTo = Role
    static constraints = {
		name(nullable: false, blank: false)
    }
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'COMPONENT'
	}
	@Override
	public String toString() {
		return name;
	}
}
