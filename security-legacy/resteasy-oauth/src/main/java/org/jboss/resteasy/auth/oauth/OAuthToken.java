package org.jboss.resteasy.auth.oauth;

/**
 * Represents either an OAuth Access or Request Token.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class OAuthToken {
    
    private String token;
    private String secret;
    private String[] scopes;
    private String[] permissions;
    private OAuthConsumer consumer;
    private long timeToLive;
    private long timestamp;
    
    public OAuthToken(String token, String secret, 
                      String[] scopes, String[] permissions, 
                      long timeToLive, OAuthConsumer consumer) {
        this.token = token;
        this.secret = secret;
        this.scopes = scopes;
        this.permissions = permissions;
        this.timeToLive = timeToLive;
        this.consumer = consumer;
        this.timestamp = System.currentTimeMillis();
    }

    
	/**
	 * Returns this Token's Consumer.
	 * @return consumer
	 */
	public OAuthConsumer getConsumer() {
	    return consumer;
	}

	/**
	 * Returns this Token's Token.
	 * @return token
	 */
	public String getToken() {
	    return token;
	}
	
	/**
	 * Returns this Token's Secret.
	 * @return secret
	 */
	public String getSecret() {
	    return secret;
	}
	
	/**
     * Returns this Token's Scopes.
     * @return scopes
     */
    public String[] getScopes() {
        return scopes;
    }
	
    /**
     * Returns this Token's permissions.
     * @return permissions
     */
    public String[] getPermissions() {
        return permissions;    
    }
    
	/**
     * Returns this Token's timestamp.
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns this Token's timeToLive.
     * @return time to live
     */
    public long getTimeToLive() {
        return timeToLive;
    }
	
	
}
