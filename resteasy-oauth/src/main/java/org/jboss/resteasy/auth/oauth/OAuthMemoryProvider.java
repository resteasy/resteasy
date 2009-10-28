package org.jboss.resteasy.auth.oauth;

import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * OAuthProvider that keeps all data in memory. Mainly used as an example and for tests.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class OAuthMemoryProvider implements OAuthProvider {
	
	private static class Consumer implements OAuthConsumer {
		private String consumerKey;
		private String consumerSecret;
		private Map<String, Token> tokens = Collections.synchronizedMap(new HashMap<String, Token>());

		public Consumer(String consumerKey, String consumerSecret) {
			this.consumerKey = consumerKey;
			this.consumerSecret = consumerSecret;
		}
		
		public String getKey() {
			return consumerKey;
		}
		public String getSecret() {
			return consumerSecret;
		}
		public RequestToken getRequestToken(String requestKey) throws OAuthException{
			// get is atomic
			Token ret = tokens.get(requestKey);
			if(ret == null || !ret.isRequestToken())
				throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such request key "+requestKey);
			return (RequestToken)ret;
		}

		public Token getAccessToken(String accessKey) throws OAuthException{
			// get is atomic
			Token ret = tokens.get(accessKey);
			if(ret == null || ret.isRequestToken())
				throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such access key "+accessKey);
			return ret;
		}

		public OAuthToken makeRequestTokens(String callback) {
			// generation of a new token must be synchronized
			synchronized(tokens){
				String newToken;
				do{
					newToken = makeRandomString();
				}while(tokens.containsKey(newToken));
				RequestToken token = new RequestToken(this, newToken, makeRandomString(), callback);
				tokens.put(token.getToken(), token);
				return token;
			}
		}

		public long verifyAndRemoveRequestToken(String requestToken, String verifier) throws OAuthException {
			// removal of request token must be synchronized
			synchronized(tokens){
				RequestToken request = getRequestToken(requestToken);
				// check the verifier, which is only set when the request token was accepted
				request.checkVerifier(verifier);
				// then let's go through and exchange this for an access token
				tokens.remove(requestToken);
				return request.timestamp;
			}
		}
		public OAuthToken makeAccessTokens(String requestToken, long timestamp) throws OAuthException {
			// generation of a new token must be synchronized
			synchronized(tokens){
				// make the access token start with the request token's timestamp 
				String newToken;
				do{
					newToken = makeRandomString();
				}while(tokens.containsKey(newToken));
				Token token = new Token(this, newToken, makeRandomString(), timestamp);
				tokens.put(token.getToken(), token);
				return token;
			}
		}
	}

	private static class RequestToken extends Token {

		private String verifier;
		private String callback;

		public RequestToken(Consumer consumer, String token, String secret, String callback) {
			super(consumer, token, secret);
			this.callback = callback;
		}

		public void checkVerifier(String verifier) throws OAuthException {
			if(this.verifier == null || this.verifier.length() == 0)
				throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Request token was not authorized "+token);
			if(verifier == null || !verifier.equals(this.verifier))
				throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid verifier code for token "+token);
		}


		public String getVerifier() {
			return verifier;
		}

		public void setVerifier(String verifier) {
			this.verifier = verifier;
		}

		public String getCallback() {
			return callback;
		}
		
		@Override
		public boolean isRequestToken() {
			return true;
		}

		public String authorise() throws OAuthException {
			if(this.verifier != null && this.verifier.length() != 0)
				throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Request token was already authorized "+token);
			this.verifier = makeRandomString();
			return verifier;
		}
	}
	
 	private static class Token implements OAuthToken {
		protected String token;
		protected String secret;
		protected long timestamp;
		private Set<String> roles;
		private String principalName;
		private Consumer consumer;

		public Token(Consumer consumer, String token, String secret) {
			this.consumer = consumer;
			this.token = token;
			this.secret = secret;
		}

		public Token(Consumer consumer, String token, String secret,
				long timestamp) {
			this(consumer, token, secret);
			this.timestamp = timestamp;
		}

		public boolean isRequestToken() {
			return false;
		}

		public long getTimestamp() {
			return timestamp;
		}
		
		/*
		 * This is synchronized to make sure the timestamp we check is the one we accept  
		 */
		public synchronized void setTimestamp(long timestamp) throws OAuthException {
			if(this.timestamp > timestamp)
				throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid timestamp "+timestamp);
			this.timestamp = timestamp;
		}

		public String getToken() {
			return token;
		}
		public String getSecret() {
			return secret;
		}

		public String getPrincipalName() {
			return principalName;
		}

		public Principal getPrincipal(){
			final String principalName = this.principalName;
			return new Principal(){
				public String getName() {
					return principalName;
				}
			};
		}
		
		public Set<String> getRoles() {
			return roles;
		}

		public void setRoles(Set<String> roles) {
			this.roles = roles;
		}

		public void setPrincipalName(String principalName) {
			this.principalName = principalName;
		}

		public Consumer getConsumer() {
			return consumer;
		}
		
	}

	private String realm;
	private Map<String, Consumer> consumers = Collections.synchronizedMap(new HashMap<String,Consumer>());

	public OAuthMemoryProvider(String realm){
		this.realm = realm;
	}
	
	private static String makeRandomString(){
		return UUID.randomUUID().toString();
	}

	//
	// For subclassers
	
	protected void addConsumer(String consumerKey, String consumerSecret){
		consumers.put(consumerKey, new Consumer(consumerKey, consumerSecret));
	}

	protected void addRequestKey(String consumerKey, String requestToken, String requestSecret, String callback) throws OAuthException{
		Consumer consumer = _getConsumer(consumerKey);
		Token token = new RequestToken(consumer, requestToken, requestSecret, callback);
		consumer.tokens.put(requestToken, token);
	}
	
	protected void addAccessKey(String consumerKey,	String accessToken, String accessSecret, String principalName, String... roles) throws OAuthException {
		Consumer consumer = _getConsumer(consumerKey);
		Token token = new Token(consumer, accessToken, accessSecret, 0);
		token.setPrincipalName(principalName);
		token.setRoles(new HashSet<String>(Arrays.asList(roles)));
		consumer.tokens.put(accessToken, token);
	}

	protected void authoriseRequestToken(String consumerKey, String requestToken, String verifier) throws OAuthException{
		_getConsumer(consumerKey).getRequestToken(requestToken).setVerifier(verifier);
	}

	protected Consumer _getConsumer(String consumerKey) throws OAuthException{
		Consumer ret = consumers.get(consumerKey); 
		if(ret == null)
			throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such consumer key "+consumerKey);
		return ret;
	}

	//
	// OAuthProvider interface
	
	public String getRealm() {
		return realm;
	}

	public String authoriseRequestToken(String consumerKey, String requestToken) throws OAuthException{
		return _getConsumer(consumerKey).getRequestToken(requestToken).authorise();
	}

	public OAuthConsumer getConsumer(String consumerKey) throws OAuthException {
		return _getConsumer(consumerKey);
	}

	public OAuthToken getRequestToken(String consumerKey, String requestToken)
	throws OAuthException {
		return _getConsumer(consumerKey).getRequestToken(requestToken);
	}

	public OAuthToken getAccessToken(String consumerKey, String accessToken)
	throws OAuthException {
		return _getConsumer(consumerKey).getAccessToken(accessToken);
	}

	public void checkTimestamp(OAuthToken token, long timestamp) throws OAuthException {
		((Token)token).setTimestamp(timestamp);
	}

	public OAuthToken makeAccessToken(String consumerKey,
			String requestToken, String verifier) throws OAuthException {
		Consumer consumer = _getConsumer(consumerKey);
		long timestamp = consumer.verifyAndRemoveRequestToken(requestToken, verifier);
		return consumer.makeAccessTokens(requestToken, timestamp);
	}

	public OAuthToken makeRequestToken(String consumerKey, String callback)
			throws OAuthException {
		return _getConsumer(consumerKey).makeRequestTokens(callback);
	}

}
