package org.jboss.resteasy.jose.jwe.crypto;


import net.jcip.annotations.Immutable;


/**
 * Authenticated cipher text. This class is immutable.
 *
 * @author Vladimir Dzhuvinov
 * @Version $version$ (2013-05-06)
 */
@Immutable
final class AuthenticatedCipherText
{


	/**
	 * The cipher text.
	 */
	private final byte[] cipherText;


	/**
	 * The authentication tag.
	 */
	private final byte[] authenticationTag;


	/**
	 * Creates a new authenticated cipher text.
	 *
	 * @param cipherText        The cipher text. Must not be {@code null}.
	 * @param authenticationTag The authentication tag. Must not be 
	 *                          {@code null}.
	 */
	public AuthenticatedCipherText(final byte[] cipherText, final byte[] authenticationTag) {

		if (cipherText == null)
			throw new IllegalArgumentException("The cipher text must not be null");

		this.cipherText = cipherText;


		if (authenticationTag == null)
			throw new IllegalArgumentException("The authentication tag must not be null");

		this.authenticationTag = authenticationTag;
	}


	/**
	 * Gets the cipher text.
	 *
	 * @return The cipher text.
	 */
	public byte[] getCipherText() {

		return cipherText;
	}


	/**
	 * Gets the authentication tag.
	 *
	 * @return The authentication tag.
	 */
	public byte[] getAuthenticationTag() {

		return authenticationTag;
	}
}