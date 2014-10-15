package org.jboss.resteasy.auth.oauth;

import java.net.HttpURLConnection;

import org.jboss.resteasy.auth.oauth.i18n.Messages;

/**
 * Represents either an OAuth Access or Request Token.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class OAuthRequestToken extends OAuthToken {
    
    private String callback;
    private String verifier;
    
    public OAuthRequestToken(String token, String secret, String callback,
                             String[] scopes, String[] permissions, 
                             long timeToLive, OAuthConsumer consumer) {
        super(token, secret, scopes, permissions, timeToLive, consumer);
        this.callback = callback;
    }
    /**
     * Returns this Token's callback
     */
    public String getCallback() {
        return callback;
    }
    
    /**
     * Returns this Token's verifier
     */
    public String getVerifier() {
        synchronized (this) {
            return verifier;
        }
    }
    
    public void setVerifier(String verifier) throws OAuthException {
        synchronized (this) {
            if (this.verifier != null) {
               throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.requestTokenAlreadyAuthorized());
            }
            this.verifier = verifier;
        }
    }
    
}
