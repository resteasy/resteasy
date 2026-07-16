package org.jboss.resteasy.jose.jwe.crypto;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.resteasy.jose.i18n.Messages;

/**
 * RSAES OAEP methods for Content Encryption Key (CEK) encryption and
 * decryption.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-06)
 */
class RSA_OAEP {

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
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            return cipher.doFinal(cek.getEncoded());
        } catch (Exception e) {
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
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, priv);
            byte[] secretKeyBytes = cipher.doFinal(encryptedCEK);
            return new SecretKeySpec(secretKeyBytes, "AES");

        } catch (Exception e) {
            throw new RuntimeException(Messages.MESSAGES.couldntDecryptCEK(e.getLocalizedMessage()), e);
        }
    }

    /**
     * Prevents public instantiation.
     */
    private RSA_OAEP() {
    }
}
