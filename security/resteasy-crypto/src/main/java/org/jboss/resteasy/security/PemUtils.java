package org.jboss.resteasy.security;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Utility classes to extract PublicKey, PrivateKey, and X509Certificate from openssl generated PEM files.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PemUtils {
    static {
        BouncyIntegration.init();
    }

    public static X509Certificate decodeCertificate(InputStream is) throws Exception {
        byte[] der = pemToDer(is);
        ByteArrayInputStream bis = new ByteArrayInputStream(der);
        return DerUtils.decodeCertificate(bis);
    }

    public static X509Certificate decodeCertificate(String cert) throws Exception {
        byte[] der = pemToDer(cert);
        ByteArrayInputStream bis = new ByteArrayInputStream(der);
        return DerUtils.decodeCertificate(bis);
    }

    /**
     * Extract a public key from a PEM string.
     *
     * @param pem PEM encoded string
     * @return {@link PublicKey}
     * @throws Exception if error occurred
     */
    public static PublicKey decodePublicKey(String pem) throws Exception {
        byte[] der = pemToDer(pem);
        return DerUtils.decodePublicKey(der);
    }

    /**
     * Extract a private key that is a PKCS8 pem string (base64 encoded PKCS8).
     *
     * @param pem PEM encoded string
     * @return {@link PrivateKey}
     * @throws Exception if error occurred
     */
    public static PrivateKey decodePrivateKey(String pem) throws Exception {
        byte[] der = pemToDer(pem);
        return DerUtils.decodePrivateKey(der);
    }

    public static PrivateKey decodePrivateKey(InputStream is) throws Exception {
        String pem = pemFromStream(is);
        return decodePrivateKey(pem);
    }

    /**
     * Decode a PEM file to DER format.
     *
     * @param is input stream
     * @return decoded bytes
     * @throws IOException if I/O error occurred
     */
    public static byte[] pemToDer(InputStream is) throws IOException {
        String pem = pemFromStream(is);
        byte[] der = pemToDer(pem);
        return der;
    }

    /**
     * Decode a PEM string to DER format.
     *
     * @param pem PEM encoded string
     * @return decoded bytes
     * @throws java.io.IOException if I/O error occurred
     */
    public static byte[] pemToDer(String pem) throws java.io.IOException {
        pem = removeBeginEnd(pem);
        return Base64.getMimeDecoder().decode(pem);
    }

    public static String removeBeginEnd(String pem) {
        pem = pem.replaceAll("-----BEGIN (.*)-----", "");
        pem = pem.replaceAll("-----END (.*)----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }

    public static String pemFromStream(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        byte[] keyBytes = new byte[dis.available()];
        dis.readFully(keyBytes);
        dis.close();
        return new String(keyBytes, StandardCharsets.UTF_8);
    }
}
