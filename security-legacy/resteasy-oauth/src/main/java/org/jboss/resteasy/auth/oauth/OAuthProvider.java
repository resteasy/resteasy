package org.jboss.resteasy.auth.oauth;

import java.util.Set;

/**
 * Implement this interface to provide the RESTEasy servlets and filters with the knowledge to
 * load and store OAuth Consumer, Request and Access Tokens.
 * 
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public interface OAuthProvider extends OAuthConsumerRegistration {
	/**
	 * Returns the Realm of this provider.
	 * @return Realm of this provider
	 */
	public String getRealm();

	/**
	 * Returns the OAuth Consumer for the given Consumer key. If no such Consumer exists, throw an OAuthException.
	 * @param consumerKey the Consumer key to load.
	 * @return the OAuth Consumer for the given Consumer key.
	 * @throws OAuthException thrown if the given Consumer does not exist.
	 */
	public OAuthConsumer getConsumer(String consumerKey)throws OAuthException;
	
	/**
	 * Returns the OAuth Request Token for the given Consumer key and Request Token. If no such Consumer or Request Token exist, throw an OAuthException.
	 * @param consumerKey the Consumer key whose Request Token we want to load
	 * @param requestToken the Request Token to load
	 * @return the OAuth Request Token for the given Consumer key and Request Token
	 * @throws OAuthException thrown if the given Request Token does not exist.
	 */
	public OAuthRequestToken getRequestToken(String consumerKey, String requestToken) throws OAuthException;

	/**
	 * Returns the OAuth Access Token for the given Consumer key and Access Token. If no such Consumer or Access Token exist, throw an OAuthException.
	 * @param consumerKey the Consumer key whose Access Token we want to load
	 * @param accessToken the Access Token to load
	 * @return the OAuth Access Token for the given Consumer key and Access Token
	 * @throws OAuthException thrown if the given Consumer or Access Token do not exist.
	 */
	public OAuthToken getAccessToken(String consumerKey, String accessToken) throws OAuthException;
	
	/**
	 * Make a new OAuth Request Token for the given Consumer, using the given callback.
	 * @param consumerKey the Consumer key for whom to create a new Request Token
	 * @param callback the Client-specified callback for this Request Token
	 * @param scopes resource URIs the consumer would like to access
	 * @param permissions permissions the consumer is requesting
	 * @return a new OAuth Request Token for the given Consumer
	 * @throws OAuthException thrown if the given Consumer does not exist
	 */
	public OAuthToken makeRequestToken(String consumerKey, String callback, 
	        String[] scopes, String[] permissions) throws OAuthException;

	/**
	 * Make a new OAuth Access Token for the given Consumer, using the given Request Token and Verifier. 
	 * If the Request Token has not yet been authorised and/or does not match the given Specifier, throw an OAuthException.
	 * @param consumerKey the Consumer key for whom to create a new Access Token
	 * @param requestToken the Request Token to exchange for a new Access Token
	 * @param verifier the Client-specified Verifier that must match the Verifier that was given to the Client
	 * when the given Request Token was authorised.
	 * @return a new OAuth Access Token for the given Consumer
	 * @throws OAuthException thrown if the given Consumer or Request Token does not exist, if the Request Token is not authorised 
	 * or if the Verifier is invalid. 
	 */
	public OAuthToken makeAccessToken(String consumerKey, String requestToken, String verifier) throws OAuthException;

	/**
	 * Authorises the given Request Token for the given Consumer and return a new Verifier to be returned to the Client.
	 * If the given Consumer or Request Token do not exist, or if the Request Token has already been authorised, throw an OAuthException.
	 * @param consumerKey the Consumer Key whose Request Token we want to authorise
	 * @param requestToken the Request Token to authorise
	 * @return a Verifier associated with the newly-authorised Request Token.
	 * @throws OAuthException thrown if the given Consumer or Request Token do not exist, or if the Request Token has already been authorised.
	 */
	public String authoriseRequestToken(String consumerKey, String requestToken) throws OAuthException;

	/**
	 * Checks that the given timestamp is valid for the given OAuth Token. The timestamp should always be
	 * greater or equal to the last timestamp used for the given OAuth Token. The responsability to know whether
	 * the given OAuth Token is a Request or Access Token is left to the implementer. This method should associate 
	 * and remember the given timestamp for the given Token if it is valid, since the message integrity has
	 * already been verified and we are guaranteed that the given timestamp comes from a message signed
	 * from the appropriate Consumer.
	 * @param token the OAuth Token whose timestamp to check and save if valid
	 * @param timestamp the timestamp to check and save if valid
	 * @throws OAuthException thrown if the given timestamp is not greater or equal to the last timestamp associated 
	 * with the given OAuth Token
	 */
	public void checkTimestamp(OAuthToken token, long timestamp) throws OAuthException;
	
	/**
	 * Converts custom permissions which may have been associated with consumers
	 * or access tokens into domain specific roles, example, 
	 * given a "printResources" permission this method may return a role name "printerService"
	 * @param permissions array of permissions 
	 * @return roles set of roles
	 */
	public Set<String> convertPermissionsToRoles(String[] permissions);

}
