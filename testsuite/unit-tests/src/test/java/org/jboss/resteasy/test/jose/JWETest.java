package org.jboss.resteasy.test.jose;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.jboss.logging.Logger;
import org.jboss.resteasy.jose.jwe.JWEBuilder;
import org.jboss.resteasy.jose.jwe.JWEInput;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @tpSubChapter Jose tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for JWE
 * @tpSince RESTEasy 3.0.16
 */
@TestMethodOrder(MethodName.class)
public class JWETest {
    private static final String ERROR_MSG = "Wrong conversion";
    private static final String BOUNCY_CASTLE_ERROR = "BouncyCastle security provider can't be used in non-OpenJDK (missing signatures)";
    protected final Logger logger = Logger.getLogger(JWETest.class.getName());

    /**
     * @tpTestDetails RSA test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRSA() throws Exception {
        Assumptions.assumeTrue(TestUtil.isOpenJDK(), TestUtil.getErrorMessageForKnownIssue("JBEAP-3550", BOUNCY_CASTLE_ERROR));
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        String content = "Live long and prosper.";

        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).RSA1_5((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assertions.assertEquals(content, from, ERROR_MSG);
        }
        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).RSA_OAEP((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assertions.assertEquals(content, from, ERROR_MSG);
        }
        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).A128CBC_HS256()
                    .RSA1_5((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assertions.assertEquals(content, from, ERROR_MSG);
        }
        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).A128CBC_HS256()
                    .RSA_OAEP((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assertions.assertEquals(content, from, ERROR_MSG);
        }
    }

    /**
     * @tpTestDetails Direct test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDirect() throws Exception {
        Assumptions.assumeTrue(TestUtil.isOpenJDK(),
                TestUtil.getErrorMessageForKnownIssue("JBEAP-3550", BOUNCY_CASTLE_ERROR));
        String content = "Live long and prosper.";
        String encoded = new JWEBuilder().contentBytes(content.getBytes()).dir("geheim");
        logger.info("encoded: " + encoded);
        byte[] raw = new JWEInput(encoded).decrypt("geheim").getRawContent();
        String from = new String(raw);
        Assertions.assertEquals(content, from, ERROR_MSG);

    }
}
