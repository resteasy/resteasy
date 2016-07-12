package org.jboss.resteasy.test.crypto;

import org.apache.logging.log4j.LogManager;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;

/**
 * @tpSubChapter Crypto
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for DosetaKeyRepository without EAP and arquillian.
 * @tpSince RESTEasy 3.0.16
 */
public class LocalTest {
    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LocalTest.class.getName());
    public static KeyPair keys;
    public static DosetaKeyRepository repository;
    static final String filePath = TestUtil.getResourcePath(LocalTest.class, "LocalTest.jks");
    private static final String ERROR_MSG = "DosetaKeyRepository works incorrectly";

    @BeforeClass
    public static void setup() throws Exception {
        Logger.setLoggerType(Logger.LoggerType.JUL);
        repository = new DosetaKeyRepository();
        repository.setKeyStoreFile(filePath);
        repository.setKeyStorePassword("password");
        repository.setUseDns(false);
        repository.start();

        PrivateKey privateKey = repository.getKeyStore().getPrivateKey("test._domainKey.samplezone.org");
        if (privateKey == null) {
            throw new Exception("Private Key is null.");
        }
        PublicKey publicKey = repository.getKeyStore().getPublicKey("test._domainKey.samplezone.org");
        keys = new KeyPair(publicKey, privateKey);

        KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

    @Test
    public void testAttributes() throws Exception {
        DKIMSignature signed = new DKIMSignature();
        signed.setAttribute("path", "/hello/world");
        signed.setTimestamp();
        signed.addHeader("Visa");
        signed.addHeader("Visa");
        MultivaluedMapImpl<String, String> headers = new MultivaluedMapImpl<String, String>();
        headers.add("Visa", "v1");
        headers.add("Visa", "v2");
        headers.add("Visa", "v3");
        signed.sign(headers, null, keys.getPrivate());

        String signedHeader = signed.toString();

        logger.info(signedHeader);

        DKIMSignature verified = new DKIMSignature(signedHeader);

        HashMap<String, String> requiredAttributes = new HashMap<String, String>();
        requiredAttributes.put("path", "/hello/world");

        Verification verification = new Verification();
        verification.getRequiredAttributes().put("path", "/hello/world");

        MultivaluedMap<String, String> verifiedHeaders = verification.verify(verified, headers, null, keys.getPublic());
        Assert.assertEquals(verifiedHeaders.size(), 1);
        List<String> visas = verifiedHeaders.get("Visa");
        Assert.assertNotNull(ERROR_MSG, visas);
        Assert.assertEquals(ERROR_MSG, visas.size(), 2);
        logger.info(visas);
        Assert.assertEquals(ERROR_MSG, visas.get(0), "v3");
        Assert.assertEquals(ERROR_MSG, visas.get(1), "v2");
    }

    @Test
    public void testBadAttributes() throws Exception {
        DKIMSignature signed = new DKIMSignature();
        signed.setAttribute("path", "/hello/world");
        signed.setTimestamp();
        signed.addHeader("Visa");
        signed.addHeader("Visa");
        MultivaluedMapImpl<String, String> headers = new MultivaluedMapImpl<String, String>();
        headers.add("Visa", "v1");
        headers.add("Visa", "v2");
        headers.add("Visa", "v3");
        signed.sign(headers, null, keys.getPrivate());

        String signedHeader = signed.toString();

        logger.info(signedHeader);

        DKIMSignature verified = new DKIMSignature(signedHeader);

        HashMap<String, String> requiredAttributes = new HashMap<String, String>();
        requiredAttributes.put("path", "/hello/world");

        Verification verification = new Verification();
        verification.getRequiredAttributes().put("path", "/hello");

        try {
            verification.verify(verified, headers, null, keys.getPublic());
            Assert.fail("Verification was successful, but it shoudn't be");
        } catch (SignatureException e) {
        }
    }
}
