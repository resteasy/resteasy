package org.jboss.resteasy.auth.oauth;

/**
 * Represents either an OAuth Access or Request Token.
 * @author Stéphane Épardaud <stef@epardaud.fr>
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
     * Returns this Token's permissions
     */
    public String[] getPermissions() {
        return permissions;    
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
	
	
}
