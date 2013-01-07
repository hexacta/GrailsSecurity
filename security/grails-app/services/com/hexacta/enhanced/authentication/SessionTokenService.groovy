package com.hexacta.enhanced.authentication

import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional;

class SessionTokenService {
	private Logger LOGGER = Logger.getLogger(SessionTokenService.class)
	
	def authenticationService
	def userLogin
	
	@Transactional
	void destroy(){
		AuthenticationUser.withTransaction { status ->
			def user = AuthenticationUser.findByLogin(userLogin)
			if(user){
				LOGGER.info("Session timeout. Logging out user ${user.login}")
				user.sessionToken = null
				user.save(flush: true)
			}
		}
	}
	
	void init(){
		LOGGER.debug("SessionToken initialized")
		userLogin = authenticationService.getUserPrincipal().login
	}
}
