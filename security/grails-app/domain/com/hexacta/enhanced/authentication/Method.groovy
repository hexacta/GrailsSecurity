package com.hexacta.enhanced.authentication
import org.springframework.context.i18n.LocaleContextHolder

class Method {
	def grailsApplication
	String name
	String label
	
	static belongsTo = ControllerConfiguration
	static transients = ['grailsApplication']
	static constraints = {
		name(nullable: false, blank: false)
		label(nullable:false, blank: false)
	}
	static mapping = {
		table AuthenticationUtils.DOMAIN_PREFFIX + 'METHOD'
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
