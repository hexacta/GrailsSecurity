package com.hexacta.enhanced.authentication

import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional;

class SessionTokenService implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient Logger LOGGER = Logger.getLogger(SessionTokenService.class)
	
	def transient authenticationService
	String userLogin
	
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
