package com.hexacta.enhanced.authentication

class Role {
	String name
	boolean allowDeletion = true
	
	static hasMany = [roles: Role, permissions: Permission, components: Component]
	
    static constraints = {
		name(blank:false, unique: true)
		roles(nullable: true)
		components(nullable: true)
		permissions(nullable: true)
    }
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'ROLE'
	}

	@Override
	public String toString() {
		return name;
	}
}
