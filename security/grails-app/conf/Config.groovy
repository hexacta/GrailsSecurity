// configuration for plugin testing - will not be included in the plugin zip

log4j = {
	root {
		info 'stdout'
	}
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

grails.views.javascript.library="jquery"

enhanced.authentication.passwordResetTimeout = 30
enhanced.authentication.forbidMultipleSessions = false
enhanced.authentication.disable.bootstrap.controllers = false
enhanced.authentication.disable.filter = false
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
/*
grails.plugins.springsecurity.secureChannel.definition = [
	'/**':         'REQUIRES_SECURE_CHANNEL'
  ]
*/