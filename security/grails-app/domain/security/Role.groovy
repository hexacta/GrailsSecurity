package security

class Role {
	String name
	static belongsTo = AuthenticationUser
	static hasMany = [roles: Role, permissions: Permission, components: Component]
	
    static constraints = {
		name(blank:false)
		roles(nullable: true)
		components(nullable: true)
		permissions(nullable: true)
    }
	
	@Override
	public String toString() {
		return name;
	}
}
