class EnhancedAuthenticationGrailsPlugin {
    // the plugin version
    def version = "2.3.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
		"grails-app/controllers/test/TestController.groovy",
		"grails-app/views/test/test.gsp",
		"grails-app/views/layouts/main.gsp",
		"grails-app/conf/RuleSet.groovy"
		]

    def title = "Enhanced Authentication Plugin" // Headline display name of the plugin
    def author = "Hexacta"
    def authorEmail = "hat@hexacta.com"
    def description = '''\
Security plugin based on the Authentication plugin
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/enhanced-authentication"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Hexacta", url: "http://www.hexacta.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
		sessionTokenService(com.hexacta.enhanced.authentication.SessionTokenService) { bean ->
			bean.scope = 'session'
			authenticationService = ref("authenticationService")
			bean.destroyMethod = 'destroy'
		}
		
		sessionScopeProxy(org.springframework.aop.scope.ScopedProxyFactoryBean) {
			targetBeanName = 'sessionTokenService'
			proxyTargetClass = true
		}
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
		def gspResources
		if(application.warDeployed){
			gspResources = applicationContext.getResources("**/*.gsp")?.toList()
		}
		else {
			gspResources = applicationContext.getResources("file:**/*.gsp")?.toList()
		}
		
		def slurper = new XmlSlurper(new org.cyberneko.html.parsers.SAXParser())
		for(resource in gspResources){
			def htmlParser = slurper.parse(resource.file)
			htmlParser.'**'.findAll { it.name().toLowerCase() == "security:id"}.each {
				applicationContext.authenticationService.registerComponent(it.@id.text())
			}
		}
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
