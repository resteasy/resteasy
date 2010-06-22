package org.jboss.resteasy.auth.oauth;


/**
 * Represents an OAuth Consumer.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class OAuthConsumer {
    
    private String key;
    private String secret;
    private String displayName;
    private String connectURI;
    
    public OAuthConsumer(String key, String secret, String displayName, String connectURI) {
        this.key = key;
        this.secret =  secret;
        this.displayName = displayName;
        this.connectURI = connectURI;
    }
    
	/**
	 * Returns the OAuth Consumer's key
	 */
	public String getKey() {
	    return key;
	}
	
	/**
	 * Returns the OAuth Consumer's private secret.
	 */
	public String getSecret() {
	    return secret;
	}
	
	/**
     * Returns the OAuth Consumer's display name.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Returns the OAuth Consumer's connect URI.
     * If provided then it will be used to validate callback URLs which consumer
     * will provide during request token acquisition requests 
     */
    public String getConnectURI() {
        return connectURI;
    }
}
