package com.hexacta.enhanced.authentication

class Method {
	String name
	String label
	
	static belongsTo = ControllerConfiguration
	
	static constraints = {
		name(nullable: false, blank: false)
		label(nullable:false, blank: false)
	}
	
	@Override
	public String toString() {
		return label;
	}
}
