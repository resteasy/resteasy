package org.jboss.resteasy.auth.oauth;

import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuthProvider that keeps all data in memory. Mainly used as an example and for tests.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class OAuthMemoryProvider implements OAuthProvider {
	
	private String realm;
	private ConcurrentHashMap<String, OAuthConsumer> consumers = new ConcurrentHashMap<String,OAuthConsumer>();
	private ConcurrentHashMap<String, OAuthRequestToken> requestTokens = new ConcurrentHashMap<String,OAuthRequestToken>();
	private ConcurrentHashMap<String, OAuthToken> accessTokens = new ConcurrentHashMap<String,OAuthToken>();
	
	public OAuthMemoryProvider(String realm){
		this.realm = realm;
	}

	private OAuthToken doMakeAccessTokens(OAuthRequestToken requestToken) throws OAuthException {
        String newToken;
        do{
            newToken = makeRandomString();
        }while(accessTokens.containsKey(newToken));
        OAuthToken token = new OAuthToken(newToken, makeRandomString(), 
                                          requestToken.getScopes(), -1, requestToken.getConsumer());
        accessTokens.put(token.getToken(), token);
        return token;
	}
	
	private OAuthToken doGetAccessToken(String consumerKey, String accessKey) throws OAuthException{
        // get is atomic
        OAuthToken ret = accessTokens.get(accessKey);
        if (!ret.getConsumer().getKey().equals(consumerKey)) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Consumer is invalid");
        }
        if(ret == null)
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such access key "+accessKey);
        return ret;
    }

    private OAuthRequestToken doMakeRequestToken(String consumerKey, String callback, String[] scopes) 
        throws OAuthException {
        OAuthConsumer consumer = _getConsumer(consumerKey);
        String newToken;
        do{
            newToken = makeRandomString();
        }while(requestTokens.containsKey(newToken));
        OAuthRequestToken token = 
            new OAuthRequestToken(newToken, makeRandomString(), callback, scopes, -1, consumer);
        requestTokens.put(token.getToken(), token);
        return token;
    }

    private OAuthRequestToken doGetRequestToken(String customerKey, String requestKey) throws OAuthException{
        // get is atomic
        OAuthRequestToken ret = requestTokens.get(requestKey);
        checkCustomerKey(ret, customerKey);
        if(ret == null)
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such request key "+requestKey);
        return ret;
    }
    
    public OAuthRequestToken verifyAndRemoveRequestToken(String customerKey, String requestToken, String verifier) throws OAuthException {
        OAuthRequestToken request = getRequestToken(requestToken);
        checkCustomerKey(request, customerKey);
        // check the verifier, which is only set when the request token was accepted
        if(verifier == null || !verifier.equals(request.getVerifier()))
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid verifier code for token "+requestToken);
        // then let's go through and exchange this for an access token
        return requestTokens.remove(requestToken);
    }
	
	private static String makeRandomString(){
		return UUID.randomUUID().toString();
	}

	private void checkCustomerKey(OAuthToken token, String customerKey) throws OAuthException {
	    if (customerKey != null && !customerKey.equals(token.getConsumer().getKey())) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid customer key");
        }
	}
	//
	// For subclassers
	
	protected void addConsumer(String consumerKey, String consumerSecret){
		consumers.put(consumerKey, new OAuthConsumer(consumerKey, consumerSecret, null, null));
	}

	protected void addRequestKey(String consumerKey, String requestToken, String requestSecret, String callback, String[] scopes) throws OAuthException{
	    OAuthConsumer consumer = _getConsumer(consumerKey);
		OAuthRequestToken token = new OAuthRequestToken(requestToken, requestSecret, callback, scopes, -1, consumer);
		requestTokens.put(requestToken, token);
	}
	
	protected void addAccessKey(String consumerKey,	String accessToken, String accessSecret, String principalName, String... roles) throws OAuthException {
		OAuthConsumer consumer = _getConsumer(consumerKey);
		TokenWithCredentials token = new TokenWithCredentials(accessToken, accessSecret, null, -1, consumer);
		token.setPrincipalName(principalName);
		token.setRoles(new HashSet<String>(Arrays.asList(roles)));
		accessTokens.put(accessToken, token);
	}

	protected void authoriseRequestToken(String consumerKey, String requestToken, String verifier) throws OAuthException{
		doGetRequestToken(consumerKey, requestToken).setVerifier(verifier);
	}

	protected OAuthConsumer _getConsumer(String consumerKey) throws OAuthException{
		OAuthConsumer ret = consumers.get(consumerKey); 
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
	    String verifier = makeRandomString();
		doGetRequestToken(consumerKey, requestToken).setVerifier(verifier);
		return verifier;
	}

	public OAuthConsumer registerConsumer(String consumerKey, 
	        String displayName, String connectURI) throws OAuthException {
	    OAuthConsumer consumer = consumers.get(consumerKey);
        if (consumer != null) {
            return consumer;
        }
        consumer = new OAuthConsumer(consumerKey, makeRandomString(), displayName, connectURI);
        consumers.putIfAbsent(consumerKey, consumer);
        return consumer;
	}
	
	public OAuthConsumer getConsumer(String consumerKey) throws OAuthException {
		return _getConsumer(consumerKey);
	}

	public OAuthRequestToken getRequestToken(String consumerKey, String requestToken)
	throws OAuthException {
	    OAuthRequestToken token = getRequestToken(requestToken);
	    if (consumerKey != null && !token.getConsumer().getKey().equals(consumerKey)) {
	        throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such consumer key "+consumerKey);
	    }
		return token;
	}

	public OAuthToken getAccessToken(String consumerKey, String accessToken)
	throws OAuthException {
		return doGetAccessToken(consumerKey, accessToken);
	}

	public void checkTimestamp(OAuthToken token, long timestamp) throws OAuthException {
	    if(token.getTimestamp() > timestamp)
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid timestamp "+timestamp);
	}

	public OAuthToken makeAccessToken(String consumerKey,
			String requestToken, String verifier) throws OAuthException {
		OAuthRequestToken token = verifyAndRemoveRequestToken(consumerKey, requestToken, verifier);
		return doMakeAccessTokens(token);
	}

	public OAuthRequestToken makeRequestToken(String consumerKey, String callback, String[] scopes)
			throws OAuthException {
	    OAuthRequestToken token = doMakeRequestToken(consumerKey, callback, scopes);
		requestTokens.put(token.getToken(), token);
		return token;
	}

    public OAuthRequestToken getRequestToken(String requestToken)
            throws OAuthException {
        OAuthRequestToken token = requestTokens.get(requestToken);
        if (token == null) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such request token " + requestToken);
        }
        return token;
    }

    private static class TokenWithCredentials extends OAuthToken {
        private String principalName;
        private Set<String> roleNames;
        
        public TokenWithCredentials(String token, String secret, String[] scopes, long timeToLive,
                OAuthConsumer consumer) {
            super(token, secret, scopes, timeToLive, consumer);
        }
        
        public void setPrincipalName(String name) {
            this.principalName = name;
        }
        
        public void setRoles(Set<String> roles) {
            this.roleNames = roles;
        }
        
        @Override
        public Principal getPrincipal() {
            return new Principal() {

                public String getName() {
                    return principalName;
                };
            
            };
        }
        
        @Override
        public Set<String> getRoles() {
            return roleNames;
        }
    }
}
