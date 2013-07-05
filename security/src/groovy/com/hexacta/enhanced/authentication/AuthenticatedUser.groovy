package com.hexacta.enhanced.authentication

/**
 * Class encapsulating the result of a login. Encapsulates errors also so that
 * login failures to not use exceptions (very bad for performance and they are not ... exceptional)
 */
class AuthenticatedUser implements Serializable {
	String login
    
    /**
     * Date/time at which this user logged in
     */
	Date loginTime

	/**
	 * Is the user currently logged in
	 */
	boolean loggedIn
	
	/**
	 * True if email address has been confirmed to work
	 */
	boolean confirmed

	/**
	 * The domain object id of the user principal object for this account
	 */
	Long userObjectId
	
	/**
	 * Any application-specific attributes you wish to store for this session can go here
	 */
	def attributes = [:]
	
	/**
	 * The result code for the outcome of the last login attempt
	 */
	int result

	@Override
	public String toString() {
		login
	}
}