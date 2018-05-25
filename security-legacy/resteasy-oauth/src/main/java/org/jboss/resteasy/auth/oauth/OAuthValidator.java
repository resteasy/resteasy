package org.jboss.resteasy.auth.oauth;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.SimpleOAuthValidator;

import org.jboss.resteasy.auth.oauth.i18n.LogMessages;
import org.jboss.resteasy.auth.oauth.i18n.Messages;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * OAuth Validator implementation to check OAuth Messages
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class OAuthValidator extends SimpleOAuthValidator {

	private OAuthProvider provider;

	public OAuthValidator(OAuthProvider provider) {
		this.provider = provider;
	}

	/**
	 * @deprecated Overridden to deprecate it since we cannot hide it. at least make sure we won't use it
	 */
	@Override
	@Deprecated
	public void validateMessage(OAuthMessage message, OAuthAccessor accessor)
    throws OAuthException, IOException, URISyntaxException {
	   throw new RuntimeException(Messages.MESSAGES.doNotUseThisMethod());
	}

	/**
	 * Overridden to validate the timestamp and nonces last since they have side-effects of storing
	 * data about the message, so we have to make sure the message is valid before we do that. 
	 * @param message message
	 * @param accessor accessor
	 * @param requestToken OAuth token
	 * @throws IOException if I/O error occurred
	 * @throws OAuthException if error occurred
	 * @throws URISyntaxException uri parsing problem
	 */
	public void validateMessage(OAuthMessage message, OAuthAccessor accessor, OAuthToken requestToken)
    throws OAuthException, IOException, URISyntaxException {
		checkSingleParameters(message);
        validateVersion(message);
        validateSignature(message, accessor);
        validateTimestampAndNonce(message, requestToken);
    }

    /**
     * Throw an exception if the timestamp is out of range or the nonce has been
     * validated previously.
     * @param message message
     * @param token OAuth token
     * @throws IOException if I/O error occurred
     * @throws OAuthProblemException if error occurred
     */
    protected void validateTimestampAndNonce(OAuthMessage message, OAuthToken token)
    throws IOException, OAuthProblemException {
        message.requireParameters(OAuth.OAUTH_TIMESTAMP, OAuth.OAUTH_NONCE);
        long timestamp = Long.parseLong(message.getParameter(OAuth.OAUTH_TIMESTAMP));
        long now = currentTimeMsec();
        validateTimestamp(message, now, token);
        validateNonce(message, timestamp, now);
    }

	/**
	 * Overridden to delegate timestamp validation to the provider.
	 * @param message message
	 * @param timestamp timestamp
	 * @param token OAuth token
	 * @throws IOException if I/O error occurred
     * @throws OAuthProblemException if error occurred
	 */
    protected void validateTimestamp(OAuthMessage message, long timestamp, OAuthToken token) throws IOException,
    OAuthProblemException {
    	// this is a consumer request with no token yet
    	if(token == null)
    		return;
		try {
			provider.checkTimestamp(token, timestamp);
		} catch (org.jboss.resteasy.auth.oauth.OAuthException e) {
		   LogMessages.LOGGER.error(Messages.MESSAGES.invalidTimestamp(), e);
			throw new OAuthProblemException(OAuth.Problems.TIMESTAMP_REFUSED);
		}
	}
}
