package com.hexacta.enhanced.authentication

class ControllerConfiguration {
	def grailsApplication
	String name
	String label
	static belongsTo = Permission
	static hasMany = [methods: Method]
	static transients = ['grailsApplication']
	
    static constraints = {
		name(nullable: false, blank: false, unique: true)
		label(nullable:false, blank: false)
    }
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'CONTROLLER_CONFIGURATION'
	}
	@Override
	public String toString() {
		return label
	}
}
