package org.jboss.resteasy.auth.oauth;

import java.security.Principal;
import java.util.Set;

/**
 * Represents either an OAuth Access or Request Token.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public interface OAuthToken {
	/**
	 * Returns this Token's Consumer.
	 */
	OAuthConsumer getConsumer();

	/**
	 * Returns this Token's Token
	 */
	String getToken();

	/**
	 * Returns this Token's Secret
	 */
	String getSecret();
	
	/**
	 * Returns this Token's Principal. This is the Principal that will be set for a Request Token when
	 * the User authorises it, and will be returned by the Access Token to be used to set the logged in
	 * Principal when the Consumer authenticates using this OAuth Access Token.
	 */
	Principal getPrincipal();

	/**
	 * Returns this Token's role list. This is the role list that will be set for a Request Token when
	 * the User authorises it, and will be returned by the Access Token to be used to set the logged in
	 * role list when the Consumer authenticates using this OAuth Access Token.
	 */
	Set<String> getRoles();
}
