package org.jboss.resteasy.jose.jwe.crypto;


import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;


/**
 * AES encryption, decryption and key generation methods. Uses the 
 * BouncyCastle.org provider.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-06)
 */
class AES
{


	/**
	 * Generates an AES key of the specified length.
	 *
	 * @param keyBitLength The key length, in bits.
	 *
	 * @return The AES key.
	 *
	 * @throws RuntimeException If an AES key couldn't be generated.
	 */
	public static SecretKey generateKey(final int keyBitLength) 
		throws RuntimeException {

		KeyGenerator keygen;

		try {
			keygen = KeyGenerator.getInstance("AES", new BouncyCastleProvider());

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException(e.getMessage(), e);
		}

		keygen.init(keyBitLength);
		return keygen.generateKey();
	}


	/**
	 * Creates a new AES cipher.
	 *
	 * @param secretKey     The AES key. Must not be {@code null}.
	 * @param forEncryption If {@code true} creates an AES encryption
	 *                      cipher, else creates an AES decryption 
	 *                      cipher.
	 *
	 * @return The AES cipher.
	 */
	public static AESEngine createCipher(final SecretKey secretKey,
		                             final boolean forEncryption) {

		AESEngine cipher = new AESEngine();

		CipherParameters cipherParams = new KeyParameter(secretKey.getEncoded());

		cipher.init(forEncryption, cipherParams);

		return cipher;
	}


	/**
	 * Prevents public instantiation.
	 */
	private AES() { }
}