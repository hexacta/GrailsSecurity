class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:"authenticationUser", action:"index")
		"500"(view:'/error')
	}
}
