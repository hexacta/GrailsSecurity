package com.hexacta.enhanced.authentication

import org.apache.log4j.Logger

class SessionTokenService {
	private Logger LOGGER = Logger.getLogger(SessionTokenService.class)
	def authenticationService
	
	void destroy(){
		def user = authenticationService.getUserPrincipal();
		if(user){
			LOGGER.info("Session timeout. Logging out user ${user.login}")
			user.sessionToken = null;
			user.save(flush: true)
		}
	}
}
