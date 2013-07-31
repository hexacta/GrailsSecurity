package com.hexacta.enhanced.authentication

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

class ControllerConfiguration {
	def grailsApplication
	String name
	String label
	static belongsTo = Permission
	static hasMany = [methods: Method]
	
    static constraints = {
		name(nullable: false, blank: false, unique: true)
		label(nullable:false, blank: false)
    }
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'CONTROLLER_CONFIGURATION'
	}
	@Override
	public String toString() {
		def locale = LocaleContextHolder?.getLocale()
		if(locale){
			return grailsApplication?.getMainContext()?.getMessage(label, null, label, locale);
		}
		else{
			return label
		}
	}
}
