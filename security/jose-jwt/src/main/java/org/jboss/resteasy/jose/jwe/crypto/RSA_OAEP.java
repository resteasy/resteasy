package org.jboss.resteasy.jose.jwe.crypto;


import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.jboss.resteasy.jose.i18n.Messages;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


/**
 * RSAES OAEP methods for Content Encryption Key (CEK) encryption and 
 * decryption. Uses the BouncyCastle.org provider.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-06)
 */
class RSA_OAEP
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
			AsymmetricBlockCipher engine = new RSAEngine();

			// JCA identifier RSA/ECB/OAEPWithSHA-1AndMGF1Padding ?
			OAEPEncoding cipher = new OAEPEncoding(engine);

			BigInteger mod = pub.getModulus();
			BigInteger exp = pub.getPublicExponent();
			RSAKeyParameters keyParams = new RSAKeyParameters(false, mod, exp);
			cipher.init(true, keyParams);

			int inputBlockSize = cipher.getInputBlockSize();
			int outputBlockSize = cipher.getOutputBlockSize();

			byte[] keyBytes = cek.getEncoded();

			return cipher.processBlock(keyBytes, 0, keyBytes.length);

		} catch (Exception e) {

			// org.bouncycastle.crypto.InvalidCipherTextException
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
		                           final byte[] encryptedCEK)
		throws RuntimeException {

		try {
			RSAEngine engine = new RSAEngine();
			OAEPEncoding cipher = new OAEPEncoding(engine);
			
			BigInteger mod = priv.getModulus();
			BigInteger exp = priv.getPrivateExponent();

			RSAKeyParameters keyParams = new RSAKeyParameters(true, mod, exp);
			cipher.init(false, keyParams);
			byte[] secretKeyBytes = cipher.processBlock(encryptedCEK, 0, encryptedCEK.length);
			return new SecretKeySpec(secretKeyBytes, "AES");

		} catch (Exception e) {

			// org.bouncycastle.crypto.InvalidCipherTextException
		   throw new RuntimeException(Messages.MESSAGES.couldntDecryptCEK(e.getLocalizedMessage()), e);
		}
	}


	/**
	 * Prevents public instantiation.
	 */
	private RSA_OAEP() { }
}