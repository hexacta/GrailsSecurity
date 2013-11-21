package com.hexacta.enhanced.authentication

class Role {
	String name
	boolean allowDeletion = true
	Role parentRole
	static hasMany = [roles: Role, permissions: Permission, components: Component]
	
    static constraints = {
		name(blank:false, unique: true)
		roles(nullable: true)
		components(nullable: true)
		permissions(nullable: true)
    }
	static mappedBy = {
		roles: "parentRole"
	}
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'ROLE'
	}
	
	def findPossibleChildren(){
		def invalidRoles = []
		for(def role = this; role != null; role = role.parentRole) {
			if(role.id != null){
				invalidRoles << role.id
			}
		}
		invalidRoles.empty ? Role.list() : Role.findAllByIdNotInList(invalidRoles)
	}

	@Override
	public String toString() {
		return name;
	}
}
