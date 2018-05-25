package org.jboss.resteasy.auth.oauth;

import java.net.HttpURLConnection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.resteasy.auth.oauth.i18n.Messages;

/**
 * OAuthProvider that keeps all data in memory. Mainly used as an example and for tests.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class OAuthMemoryProvider implements OAuthProvider {
	
	private String realm;
	private ConcurrentHashMap<String, OAuthConsumer> consumers = new ConcurrentHashMap<String,OAuthConsumer>();
	private ConcurrentHashMap<String, OAuthRequestToken> requestTokens = new ConcurrentHashMap<String,OAuthRequestToken>();
	private ConcurrentHashMap<String, OAuthToken> accessTokens = new ConcurrentHashMap<String,OAuthToken>();
	
	public OAuthMemoryProvider(){
        this("default");
    }
	
	public OAuthMemoryProvider(String realm){
		this.realm = realm;
	}

	private OAuthToken doMakeAccessTokens(OAuthRequestToken requestToken) throws OAuthException {
        String newToken;
        do{
            newToken = makeRandomString();
        }while(accessTokens.containsKey(newToken));
        OAuthToken token = new OAuthToken(newToken, makeRandomString(), 
                                          requestToken.getScopes(), requestToken.getPermissions(),
                                          -1, requestToken.getConsumer());
        accessTokens.put(token.getToken(), token);
        return token;
	}
	
	private OAuthToken doGetAccessToken(String consumerKey, String accessKey) throws OAuthException{
        // get is atomic
        OAuthToken ret = accessTokens.get(accessKey);
        if (!ret.getConsumer().getKey().equals(consumerKey)) {
           throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.consumerIsInvalid());
        }
        if(ret == null)
           throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.noSuchAccessKey(accessKey));
        return ret;
    }

    private OAuthRequestToken doMakeRequestToken(String consumerKey, String callback, 
            String[] scopes, String[] permissions) 
        throws OAuthException {
        OAuthConsumer consumer = _getConsumer(consumerKey);
        String newToken;
        do{
            newToken = makeRandomString();
        }while(requestTokens.containsKey(newToken));
        OAuthRequestToken token = 
            new OAuthRequestToken(newToken, makeRandomString(), callback, 
                    scopes, permissions, -1, consumer);
        requestTokens.put(token.getToken(), token);
        return token;
    }

    private OAuthRequestToken doGetRequestToken(String customerKey, String requestKey) throws OAuthException{
        // get is atomic
        OAuthRequestToken ret = requestTokens.get(requestKey);
        checkCustomerKey(ret, customerKey);
        if(ret == null)
           throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.noSuchRequestKey(requestKey));
        return ret;
    }
    
    public OAuthRequestToken verifyAndRemoveRequestToken(String customerKey, String requestToken, String verifier) throws OAuthException {
        OAuthRequestToken request = getRequestToken(requestToken);
        checkCustomerKey(request, customerKey);
        // check the verifier, which is only set when the request token was accepted
        if(verifier == null || !verifier.equals(request.getVerifier()))
           throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.invalidVerifierCode(requestToken));
        // then let's go through and exchange this for an access token
        return requestTokens.remove(requestToken);
    }
	
	private static String makeRandomString(){
		return UUID.randomUUID().toString();
	}

	private void checkCustomerKey(OAuthToken token, String customerKey) throws OAuthException {
	    if (customerKey != null && !customerKey.equals(token.getConsumer().getKey())) {
	       throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.invalidCustomerKey());
        }
	}
	//
	// For subclassers
	
	protected void addConsumer(String consumerKey, String consumerSecret){
		consumers.put(consumerKey, new OAuthConsumer(consumerKey, consumerSecret, null, null));
	}

	protected void addRequestKey(String consumerKey, String requestToken, String requestSecret, String callback, String[] scopes) throws OAuthException{
	    OAuthConsumer consumer = _getConsumer(consumerKey);
		OAuthRequestToken token = new OAuthRequestToken(requestToken, requestSecret, callback,
		        scopes, null, -1, consumer);
		requestTokens.put(requestToken, token);
	}
	
	protected void addAccessKey(String consumerKey,	String accessToken, String accessSecret, String[] permissions) throws OAuthException {
		OAuthConsumer consumer = _getConsumer(consumerKey);
		OAuthToken token = new OAuthToken(accessToken, accessSecret, 
                null, permissions, -1, consumer);
		accessTokens.put(accessToken, token);
	}

	protected void authoriseRequestToken(String consumerKey, String requestToken, String verifier) throws OAuthException{
		doGetRequestToken(consumerKey, requestToken).setVerifier(verifier);
	}

	protected OAuthConsumer _getConsumer(String consumerKey) throws OAuthException{
		OAuthConsumer ret = consumers.get(consumerKey); 
		if(ret == null)
		   throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.noSuchConsumerKey(consumerKey));
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
        if (consumer == null) {
            return consumer;
        }
        consumer = new OAuthConsumer(consumerKey, "therealfrog", displayName, connectURI);
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
	        throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.noSuchConsumerKey(consumerKey));
	    }
		return token;
	}

	public OAuthToken getAccessToken(String consumerKey, String accessToken)
	throws OAuthException {
		return doGetAccessToken(consumerKey, accessToken);
	}

	public void checkTimestamp(OAuthToken token, long timestamp) throws OAuthException {
	    if(token.getTimestamp() > timestamp)
	       throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.invalidTimestampLong(timestamp));
	}

	public OAuthToken makeAccessToken(String consumerKey,
			String requestToken, String verifier) throws OAuthException {
		OAuthRequestToken token = verifyAndRemoveRequestToken(consumerKey, requestToken, verifier);
		return doMakeAccessTokens(token);
	}

	public OAuthRequestToken makeRequestToken(String consumerKey, String callback,
	        String[] scopes, String[] permissions) throws OAuthException {
	    OAuthRequestToken token = doMakeRequestToken(consumerKey, callback, scopes, permissions);
		requestTokens.put(token.getToken(), token);
		return token;
	}

    public OAuthRequestToken getRequestToken(String requestToken)
            throws OAuthException {
        OAuthRequestToken token = requestTokens.get(requestToken);
        if (token == null) {
           throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.noSuchRequestToken(requestToken));
        }
        return token;
    }

    public void registerConsumerScopes(String consumerKey, String[] scopes)
            throws OAuthException {
        OAuthConsumer consumer = _getConsumer(consumerKey);
        consumer.setScopes(scopes);
    }

    public void registerConsumerPermissions(String consumerKey,
            String[] permissions) throws OAuthException {
        // TODO Auto-generated method stub
        
    }

    public Set<String> convertPermissionsToRoles(String[] permissions) {
        // TODO Auto-generated method stub
        return null;
    }
}
