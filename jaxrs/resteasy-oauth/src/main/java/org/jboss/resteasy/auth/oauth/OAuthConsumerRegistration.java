package org.jboss.resteasy.auth.oauth;

/**
 * Registration of OAuth consumers
 * 
 */
public interface OAuthConsumerRegistration {
		
	/**
     * Creates a new OAuth Consumer
     * @param consumerKey the Consumer key.
     * @return the OAuth Consumer for the given Consumer key.
     * @throws OAuthException thrown if Consumer can not be registered.
     */
    public OAuthConsumer registerConsumer(String consumerKey, 
            String displayName, String connectURI) throws OAuthException;

    
    /**
     * Registers Consumer Scopes
     * @param consumerKey the Consumer key.
     * @return scopes the consumer scopes
     * @throws OAuthException thrown if Consumer can not be registered.
     */
    public OAuthConsumer registerConsumerScopes(String consumerKey, 
            String[] scopes) throws OAuthException;
        
}
