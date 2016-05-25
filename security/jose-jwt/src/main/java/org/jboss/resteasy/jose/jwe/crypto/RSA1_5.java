package org.jboss.resteasy.jose.jwe.crypto;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.resteasy.jose.i18n.Messages;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


/**
 * RSAES-PKCS1-V1_5 methods for Content Encryption Key (CEK) encryption and
 * decryption.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-06)
 */
class RSA1_5
{


	/**
	 * Encrypts the specified Content Encryption Key (CEK).
	 *
	 * @param pub The public RSA key. Must not be {@code null}.
	 * @param cek The Content Encryption Key (CEK) to encrypt. Must not be
	 *            {@code null}.
	 *
	 * @return The encrypted Content Encryption Key (CEK).
	 *
	 * @throws RuntimeException If encryption failed.
	 */
	public static byte[] encryptCEK(final RSAPublicKey pub, final SecretKey cek)
		throws RuntimeException {

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pub);
			return cipher.doFinal(cek.getEncoded());

		} catch (Exception e) {

			// java.security.NoSuchAlgorithmException
			// java.security.InvalidKeyException
			// javax.crypto.IllegalBlockSizeException
		   throw new RuntimeException(Messages.MESSAGES.couldntEncryptCEK(e.getLocalizedMessage()), e);
		}
	}


	/**
	 * Decrypts the specified encrypted Content Encryption Key (CEK).
	 *
	 * @param priv         The private RSA key. Must not be {@code null}.
	 * @param encryptedCEK The encrypted Content Encryption Key (CEK) to
	 *                     decrypt. Must not be {@code null}.
	 *
	 * @return The decrypted Content Encryption Key (CEK).
	 *
	 * @throws RuntimeException If decryption failed.
	 */
	public static SecretKey decryptCEK(final RSAPrivateKey priv, 
		                           final byte[] encryptedCEK,
		                           final int keyLength)
		throws RuntimeException {

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, priv);
			byte[] secretKeyBytes = cipher.doFinal(encryptedCEK);

			if (8 * secretKeyBytes.length != keyLength) {

            throw new RuntimeException(Messages.MESSAGES.cekKeyLengthMismatch(secretKeyBytes.length, keyLength));
			}

			return new SecretKeySpec(secretKeyBytes, "AES");

		} catch (Exception e) {

			// java.security.NoSuchAlgorithmException
			// java.security.InvalidKeyException
			// javax.crypto.IllegalBlockSizeException
		   throw new RuntimeException(Messages.MESSAGES.couldntDecryptCEK(e.getLocalizedMessage()), e);
		}
	}


	/**
	 * Prevents public instantiation.
	 */
	private RSA1_5() { }
}