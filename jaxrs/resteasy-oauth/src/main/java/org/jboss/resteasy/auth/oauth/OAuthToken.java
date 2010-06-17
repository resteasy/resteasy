package org.jboss.resteasy.auth.oauth;

import java.security.Principal;
import java.util.Set;

/**
 * Represents either an OAuth Access or Request Token.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class OAuthToken {
    
    private String token;
    private String secret;
    private String[] scopes;
    private OAuthConsumer consumer;
    private long timeToLive;
    private long timestamp;
    
    public OAuthToken(String token, String secret, String[] scopes, long timeToLive,
                      OAuthConsumer consumer) {
        this.token = token;
        this.secret = secret;
        this.scopes = scopes;
        this.timeToLive = timeToLive;
        this.consumer = consumer;
        this.timestamp = System.currentTimeMillis();
    }

    
	/**
	 * Returns this Token's Consumer.
	 */
	public OAuthConsumer getConsumer() {
	    return consumer;
	}

	/**
	 * Returns this Token's Token
	 */
	public String getToken() {
	    return token;
	}
	
	/**
	 * Returns this Token's Secret
	 */
	public String getSecret() {
	    return secret;
	}
	
	/**
     * Returns this Token's Scopes
     */
    public String[] getScopes() {
        return scopes;
    }
	
	/**
     * Returns this Token's timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns this Token's timeToLive
     */
    public long getTimeToLive() {
        return timeToLive;
    }
	
	/**
	 * Returns this Token's Principal. This is the Principal that will be set for a Request Token when
	 * the User authorises it, and will be returned by the Access Token to be used to set the logged in
	 * Principal when the Consumer authenticates using this OAuth Access Token.
	 */
	public Principal getPrincipal() {
	    return null;
	}

	/**
	 * Returns this Token's role list. This is the role list that will be set for a Request Token when
	 * the User authorises it, and will be returned by the Access Token to be used to set the logged in
	 * role list when the Consumer authenticates using this OAuth Access Token.
	 */
	public Set<String> getRoles() {
	    return null;    
	}
}
