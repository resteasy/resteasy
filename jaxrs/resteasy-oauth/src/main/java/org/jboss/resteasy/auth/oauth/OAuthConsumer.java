package org.jboss.resteasy.auth.oauth;

/**
 * Represents an OAuth Consumer.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public interface OAuthConsumer {
	/**
	 * Returns the OAuth Consumer's key
	 */
	String getKey();
	/**
	 * Returns the OAuth Consumer's private secret.
	 */
	String getSecret();
}
