package org.jboss.resteasy.test.jose;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.jose.jwe.JWEBuilder;
import org.jboss.resteasy.jose.jwe.JWEInput;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @tpSubChapter Jose tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for JWE
 * @tpSince RESTEasy 3.0.16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JWETest {
    private static final String ERROR_MSG = "Wrong conversion";
    private static final String BOUNCY_CASTLE_ERROR = "BouncyCastle security provider can't be used in non-OpenJDK (missing signatures)";
    protected final Logger logger = LogManager.getLogger(JWETest.class.getName());

    /**
     * @tpTestDetails RSA test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRSA() throws Exception {
        Assume.assumeTrue(TestUtil.getErrorMessageForKnownIssue("JBEAP-3550", BOUNCY_CASTLE_ERROR), TestUtil.isOpenJDK());
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        String content = "Live long and prosper.";

        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).RSA1_5((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assert.assertEquals(ERROR_MSG, content, from);
        }
        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).RSA_OAEP((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assert.assertEquals(ERROR_MSG, content, from);
        }
        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).A128CBC_HS256().RSA1_5((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assert.assertEquals(ERROR_MSG, content, from);
        }
        {
            String encoded = new JWEBuilder().contentBytes(content.getBytes()).A128CBC_HS256().RSA_OAEP((RSAPublicKey) keyPair.getPublic());
            logger.info("encoded: " + encoded);
            byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey) keyPair.getPrivate()).getRawContent();
            String from = new String(raw);
            Assert.assertEquals(ERROR_MSG, content, from);
        }
    }

    /**
     * @tpTestDetails Direct test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDirect() throws Exception {
        Assume.assumeTrue(TestUtil.getErrorMessageForKnownIssue("JBEAP-3550", BOUNCY_CASTLE_ERROR), TestUtil.isOpenJDK());
        String content = "Live long and prosper.";
        String encoded = new JWEBuilder().contentBytes(content.getBytes()).dir("geheim");
        logger.info("encoded: " + encoded);
        byte[] raw = new JWEInput(encoded).decrypt("geheim").getRawContent();
        String from = new String(raw);
        Assert.assertEquals(ERROR_MSG, content, from);

    }
}
