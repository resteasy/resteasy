package org.jboss.resteasy.auth.oauth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Represents an OAuth Consumer.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class OAuthConsumer {
    
    private String key;
    private String secret;
    private String displayName;
    private String connectURI;
    private Set<String> scopes;
    private String[] permissions;
    
    public OAuthConsumer(String key, String secret, String displayName, String connectURI) {
        this.key = key;
        this.secret =  secret;
        this.displayName = displayName;
        this.connectURI = connectURI;
    }
    
    public OAuthConsumer(String key, String secret, String displayName, String connectURI,
                         String[] perms) {
        this.key = key;
        this.secret =  secret;
        this.displayName = displayName;
        this.connectURI = connectURI;
        this.permissions = perms;
    }
    
	/**
	 * Returns the OAuth Consumer's key.
	 * @return OAuth Consumer's key
	 */
	public String getKey() {
	    return key;
	}
	
	/**
	 * Returns the OAuth Consumer's private secret.
	 * @return OAuth Consumer's private secret
	 */
	public String getSecret() {
	    return secret;
	}
	
	/**
     * Returns the OAuth Consumer's display name.
     * @return OAuth Consumer's display name 
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Returns the OAuth Consumer's connect URI.
     * If provided then it will be used to validate callback URLs which consumer
     * will provide during request token acquisition requests.
     * 
     * @return OAuth Consumer's connect URI
     */
    public String getConnectURI() {
        return connectURI;
    }
    
    /**
     * Returns the OAuth Consumer's scopes. These are the scopes the consumer
     * will be able to access directly.
     * 
     * @return OAuth Consumer's scopes
     */
    public String[] getScopes() {
        
        synchronized (this) {
            return scopes != null ? scopes.toArray(new String[]{}) : null;
        }
    }
    
    public void setScopes(String[] scopes) {
        synchronized (this) {
            this.scopes = new HashSet<String>(Arrays.asList(scopes));
        }
    }
    

    public String[] getPermissions() {
        return permissions;
    }
}
