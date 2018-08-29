package org.jboss.resteasy.jose.jwe.crypto;



import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.resteasy.jose.i18n.Messages;


/**
 * Composite key used in AES/CBC/PKCS5Padding/HMAC-SHA2 encryption. This class
 * is immutable.
 *
 * <p>See draft-ietf-jose-json-web-algorithms-10, section 4.8.2.
 *
 * <p>See draft-mcgrew-aead-aes-cbc-hmac-sha2-01
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-06)
 */
final class CompositeKey
{


	/**
	 * The input key.
	 */
	private final SecretKey inputKey;


	/**
	 * The extracted MAC key.
	 */
	private final SecretKey macKey;


	/**
	 * The extracted AES key.
	 */
	private final SecretKey encKey;


	/**
	 * The expected truncated MAC output length.
	 */
	private final int truncatedMacLength;


	/**
	 * Creates a new composite key from the specified secret key.
	 *
	 * @param inputKey The input key. Must be 256 or 512 bits long. Must 
	 *                 not be {@code null}.
	 *
	 * @throws RuntimeException If the input key length is not supported.
	 */
	CompositeKey(final SecretKey inputKey)
		throws RuntimeException {

		this.inputKey = inputKey;

		byte[] secretKeyBytes = inputKey.getEncoded();

		if (secretKeyBytes.length == 32) {

			// AES_128_CBC_HMAC_SHA_256
			// 256 bit key -> 128 bit MAC key + 128 bit AES key
			macKey = new SecretKeySpec(secretKeyBytes, 0, 16, "HMACSHA256");
			encKey = new SecretKeySpec(secretKeyBytes, 16, 16, "AES");
			truncatedMacLength = 16;

		} else if (secretKeyBytes.length == 64) {

			// AES_256_CBC_HMAC_SHA_512
			// 512 bit key -> 256 bit MAC key + 256 bit AES key
			macKey = new SecretKeySpec(secretKeyBytes, 0, 32, "HMACSHA512");
			encKey = new SecretKeySpec(secretKeyBytes, 32, 32, "AES");
			truncatedMacLength = 32;

		} else {

		   throw new RuntimeException(Messages.MESSAGES.unsupportedKeyLength());
		}
	}


	/**
	 * Gets the input key.
	 *
	 * @return The input key.
	 */
	public SecretKey getInputKey() {

		return inputKey;
	}


	/**
	 * Gets the extracted MAC key.
	 *
	 * @return The extracted MAC key.
	 */
	public SecretKey getMACKey() {

		return macKey;
	}


	/**
	 * Gets the expected truncated MAC length.
	 *
	 * @return The expected truncated MAC length, in bytes.
	 */
	public int getTruncatedMACByteLength() {

		return truncatedMacLength;
	}


	/**
	 * Gets the extracted encryption key.
	 *
	 * @return The extracted encryption key.
	 */
	public SecretKey getAESKey() {

		return encKey;
	}
}