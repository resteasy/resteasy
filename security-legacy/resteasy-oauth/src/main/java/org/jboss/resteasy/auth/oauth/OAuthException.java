package org.jboss.resteasy.auth.oauth;

/**
 * Use this exception to throw exceptions from your OAuthProvider to specify the HTTP status code
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class OAuthException extends Exception {

	private static final long serialVersionUID = 2582076698570093344L;

	private int httpCode;

	/**
	 * Builds a new OAuth exception which will result in sending the specified HTTP status code
	 * to the Client.
	 * @param httpCode the HTTP status code to return to the OAuth Client
	 * @param message the message describing the problem, also returned to the OAuth Client
	 */
	public OAuthException(int httpCode, String message) {
		super(message);
		this.httpCode = httpCode;
	}

	/**
	 * Returns the HTTP status code to return to the OAuth Client
	 * @return HTTP status code to return to the OAuth Client
	 */
	public int getHttpCode() {
		return httpCode;
	}

}
