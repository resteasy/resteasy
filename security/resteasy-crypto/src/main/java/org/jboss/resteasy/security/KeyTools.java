package org.jboss.resteasy.security;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;

import javax.security.auth.x500.X500Principal;

import org.wildfly.security.x500.cert.X509CertificateBuilder;

/**
 * Class provides utility functions for generation of V1
 * {@link java.security.cert.X509Certificate}
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 2 $
 */
public class KeyTools {
    static {
        BouncyIntegration.init();
    }

    /**
     * Generate version 1 self signed {@link java.security.cert.X509Certificate}
     *
     * @param pair the CA key pair
     * @return the x509 certificate
     * @throws Exception the exception
     */
    public static X509Certificate generateTestCertificate(KeyPair pair) {
        return generateV1SelfSignedCertificate(pair, "Test", null);
    }

    /**
     * Generate version 1 self signed {@link java.security.cert.X509Certificate}
     *
     * @param subject the subject name
     * @param issuer  the issuer name
     * @param pair    the CA key pair
     * @return the x509 certificate
     * @throws Exception the exception
     */
    public static X509Certificate generateTestCertificate(String subject,
            String issuer, KeyPair pair) {
        return generateV1SelfSignedCertificate(pair, subject, issuer);
    }

    /**
     * Generate version 1 self signed {@link java.security.cert.X509Certificate}
     *
     * @param caKeyPair the CA key pair
     * @param subject   the subject name
     * @param issuer    the issuer name
     * @return the x509 certificate
     * @throws Exception the exception
     */
    private static X509Certificate generateV1SelfSignedCertificate(KeyPair caKeyPair,
            String subject, String issuer) {
        try {
            X500Principal subjectDN = new X500Principal("CN=" + subject);
            X500Principal issuerDN = (issuer == null) ? subjectDN
                    : new X500Principal("CN=" + issuer);

            return new X509CertificateBuilder()
                    .setVersion(1)
                    .setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()))
                    .setIssuerDn(issuerDN)
                    .setSubjectDn(subjectDN)
                    .setNotValidBefore(ZonedDateTime.now().minusSeconds(10))
                    .setNotValidAfter(ZonedDateTime.now().plusSeconds(10))
                    .setPublicKey(caKeyPair.getPublic())
                    .setSigningKey(caKeyPair.getPrivate())
                    .setSignatureAlgorithmName("SHA256WithRSA")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating X509v1Certificate.", e);
        }
    }

}
