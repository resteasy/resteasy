package org.jboss.resteasy.auth.oauth;

import java.net.HttpURLConnection;

import org.jboss.resteasy.auth.oauth.i18n.Messages;

/**
 * Represents either an OAuth Access or Request Token.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
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
     * Returns this Token's callback.
     * @return callback
     */
    public String getCallback() {
        return callback;
    }
    
    /**
     * Returns this Token's verifier.
     * @return verifier
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
