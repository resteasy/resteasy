package org.jboss.resteasy.jose.jwe.crypto;



import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.resteasy.jose.i18n.Messages;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Static methods for Hash-based Message Authentication Codes (HMAC).
 *
 * @author Axel Nennker
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-03-23)
 */
class HMAC
{


	/**
	 * Computes a Hash-based Message Authentication Code (HMAC) for the
	 * specified (shared) secret and message.
	 *
	 * @param alg     The Java Cryptography Architecture (JCA) HMAC 
	 *                algorithm name. Must not be {@code null}.
	 * @param secret  The (shared) secret. Must not be {@code null}.
	 * @param message The message. Must not be {@code null}.
	 *
	 * @return A MAC service instance.
	 *
	 * @throws RuntimeException If the algorithm is not supported or the
	 *                       MAC secret key is invalid.
	 */
	public static byte[] compute(final String alg, final byte[] secret, final byte[] message)
		throws RuntimeException {

		return compute(new SecretKeySpec(secret, alg), message);
	}


	/**
	 * Computes a Hash-based Message Authentication Code (HMAC) for the
	 * specified (shared) secret key and message.
	 *
	 * @param secretKey The (shared) secret key, with the appropriate HMAC
	 *                  algorithm. Must not be {@code null}.
	 * @param message   The message. Must not be {@code null}.
	 *
	 * @return A MAC service instance.
	 *
	 * @throws RuntimeException If the algorithm is not supported or the MAC 
	 *                       secret key is invalid.
	 */
	public static byte[] compute(final SecretKey secretKey, final byte[] message)
		throws RuntimeException {

		Mac mac;

		try {
			mac = Mac.getInstance(secretKey.getAlgorithm());

			mac.init(secretKey);

		} catch (NoSuchAlgorithmException e) {

		   throw new RuntimeException(Messages.MESSAGES.unsupportedHMACalgorithm(e.getLocalizedMessage()), e);

		} catch (InvalidKeyException e) {

		   throw new RuntimeException(Messages.MESSAGES.invalidHMACkey(e.getLocalizedMessage()), e);
		}

		mac.update(message);

		return mac.doFinal();
	}
}
