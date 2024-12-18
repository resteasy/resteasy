package org.jboss.resteasy.test.crypto;

import java.lang.invoke.MethodHandles;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;

import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Crypto
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for DosetaKeyRepository without EAP and arquillian.
 * @tpSince RESTEasy 3.0.16
 */
public class LocalTest {
    protected static final LogMessages logger = Logger.getMessageLogger(MethodHandles.lookup(), LogMessages.class,
            LocalTest.class.getName());
    public static KeyPair keys;
    public static DosetaKeyRepository repository;
    static final String filePath = TestUtil.getResourcePath(LocalTest.class, "LocalTest.jks");
    private static final String ERROR_MSG = "DosetaKeyRepository works incorrectly";

    @BeforeAll
    public static void setup() throws Exception {
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
        Assertions.assertEquals(verifiedHeaders.size(), 1);
        List<String> visas = verifiedHeaders.get("Visa");
        Assertions.assertNotNull(visas, ERROR_MSG);
        Assertions.assertEquals(visas.size(), 2, ERROR_MSG);
        logger.info(visas);
        Assertions.assertEquals(visas.get(0), "v3", ERROR_MSG);
        Assertions.assertEquals(visas.get(1), "v2", ERROR_MSG);
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
            Assertions.fail("Verification was successful, but it shoudn't be");
        } catch (SignatureException e) {
        }
    }
}
