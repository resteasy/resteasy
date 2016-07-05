package org.jboss.resteasy.jose.jwe.crypto;

import org.jboss.resteasy.jose.i18n.Messages;



/**
 * The cryptographic parts of a JSON Web Encryption (JWE) object. This class is 
 * an immutable simple wrapper for returning the cipher text, initialisation 
 * vector (IV), encrypted key and authentication tag
 * implementations.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2012-05-05)
 */
public final class JWECryptoParts
{


	/**
	 * The encrypted key (optional).
	 */
	private final String encryptedKey;


	/**
	 * The initialisation vector (optional).
	 */
	private final String iv;


	/**
	 * The cipher text.
	 */
	private final String cipherText;


	/**
	 * The authentication tag (optional).
	 */
	private final String authenticationTag;


	/**
	 * Creates a new cryptograhic JWE parts instance.
	 *
	 * @param encryptedKey      The encrypted key, {@code null} if not
	 *                          required by the encryption algorithm.
	 * @param iv                The initialisation vector (IV), 
	 *                          {@code null} if not required by the 
	 *                          encryption algorithm.
	 * @param cipherText        The cipher text. Must not be {@code null}.
	 * @param authenticationTag The authentication tag, {@code null} if the 
	 *                          JWE algorithm provides built-in integrity 
	 *                          check.
	 */
	public JWECryptoParts(final String encryptedKey,
                         final String iv,
                         final String cipherText,
                         final String authenticationTag
                        ) {

		this.encryptedKey = encryptedKey;

		this.iv = iv;

		if (cipherText == null) {

		   throw new IllegalArgumentException(Messages.MESSAGES.cipherTextMustNotBeNull());
		}

		this.cipherText = cipherText;

		this.authenticationTag = authenticationTag;
	}


	/**
	 * Gets the encrypted key.
	 *
	 * @return The encrypted key, {@code null} if not required by 
	 *         the JWE algorithm.
	 */
	public String getEncryptedKey() {

		return encryptedKey;
	}


	/**
	 * Gets the initialisation vector (IV).
	 *
	 * @return The initialisation vector (IV), {@code null} if not required
	 *         by the JWE algorithm.
	 */
	public String getInitializationVector() {

		return iv;
	}


	/**
	 * Gets the cipher text.
	 *
	 * @return The cipher text.
	 */
	public String getCipherText() {

		return cipherText;
	}


	/**
	 * Gets the authentication tag.
	 *
	 * @return The authentication tag, {@code null} if the encryption
	 *         algorithm provides built-in integrity checking.
	 */
	public String getAuthenticationTag() {

		return authenticationTag;
	}

}
