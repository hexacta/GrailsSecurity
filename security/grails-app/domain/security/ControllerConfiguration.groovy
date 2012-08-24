package security

class ControllerConfiguration {
	String name
	String label
	static belongsTo = Permission
	static hasMany = [methods: Method]
	
    static constraints = {
		name(nullable: false, blank: false)
		label(nullable:false, blank: false)
    }
	
	@Override
	public String toString() {
		return label;
	}
}
