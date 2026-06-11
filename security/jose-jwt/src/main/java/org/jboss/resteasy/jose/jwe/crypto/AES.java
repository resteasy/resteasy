package org.jboss.resteasy.jose.jwe.crypto;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * AES encryption, decryption and key generation methods.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-06)
 */
class AES {

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
            keygen = KeyGenerator.getInstance("AES");

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e.getMessage(), e);
        }

        keygen.init(keyBitLength);
        return keygen.generateKey();
    }

    /**
     * Prevents public instantiation.
     */
    private AES() {
    }
}
