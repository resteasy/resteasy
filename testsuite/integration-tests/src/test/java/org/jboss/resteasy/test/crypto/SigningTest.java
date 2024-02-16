package org.jboss.resteasy.test.crypto;

import static org.hamcrest.CoreMatchers.containsString;

import java.lang.reflect.ReflectPermission;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.security.doseta.UnauthorizedSignatureException;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.test.crypto.resource.SigningProxy;
import org.jboss.resteasy.test.crypto.resource.SigningResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Crypto
 * @tpChapter Integration tests
 * @tpTestCaseDetails Signing test for RESTEasy-crypto
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SigningTest {
    public static KeyPair keys;
    public static DosetaKeyRepository repository;
    public static PrivateKey badKey;
    private static ResteasyClient client;

    protected final Logger logger = Logger.getLogger(PKCS7SignatureSmokeTest.class.getName());

    private static final String RESPONSE_ERROR_MSG = "Response contains wrong content";
    static final String testJksPath;

    static {
        testJksPath = TestUtil.getResourcePath(SigningTest.class, "SigningTest.jks");
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SigningTest.class.getSimpleName());
        war.addClass(SigningProxy.class);
        war.addAsResource(SigningTest.class.getPackage(), "SigningTest.jks", "test.jks");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.doseta.keystore.classpath", "test.jks");
        contextParams.put("resteasy.doseta.keystore.password", "password");
        contextParams.put("resteasy.context.objects",
                "org.jboss.resteasy.security.doseta.KeyRepository : org.jboss.resteasy.security.doseta.ConfiguredDosetaKeyRepository");
        contextParams.put("resteasy.doseta.use.dns", "false");
        return TestUtil.finishContainerPrepare(war, contextParams, SigningResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SigningTest.class.getSimpleName());
    }

    @BeforeAll
    public static void setup() throws Exception {
        repository = new DosetaKeyRepository();
        repository.setKeyStoreFile(testJksPath);
        repository.setKeyStorePassword("password");
        repository.setUseDns(false);
        repository.start();

        PrivateKey privateKey = repository.getKeyStore().getPrivateKey("test._domainKey.samplezone.org");
        if (privateKey == null) {
            throw new Exception("Private Key is null!!!");
        }
        PublicKey publicKey = repository.getKeyStore().getPublicKey("test._domainKey.samplezone.org");
        keys = new KeyPair(publicKey, privateKey);

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        badKey = keyPair.getPrivate();
    }

    @AfterAll
    public static void afterIt() throws Exception {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Test for "DKIM-Signature" header attribute
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestOnly() throws Exception {
        WebTarget target = client.target(generateURL("/signed/request-only"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setDomain("samplezone.org");
        contentSignature.setSelector("test");
        contentSignature.setPrivateKey(keys.getPrivate());
        contentSignature.setBodyHashRequired(false);
        contentSignature.setAttribute("method", "GET");
        contentSignature.setAttribute("uri", "/signed/request-only");
        contentSignature.setAttribute("token", "1122");
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature).delete();

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        contentSignature = new DKIMSignature(signatureHeader);
        Verification verification = new Verification(keys.getPublic());
        verification.setBodyHashRequired(false);
        verification.getRequiredAttributes().put("token", "1122");
        verification.verify(contentSignature, response.getStringHeaders(), null, keys.getPublic());
        response.close();
    }

    /**
     * @tpTestDetails Test for manual signing
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSigningManual() throws Exception {
        WebTarget target = client.target(generateURL("/signed"));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        MarshalledEntity<String> marshalledEntity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });
        Assertions.assertEquals("hello world", marshalledEntity.getEntity(), RESPONSE_ERROR_MSG);
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        logger.info(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        Assertions.assertNotNull(signatureHeader, "Missing DKIM_SIGNATURE header");

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);
        contentSignature.verify(response.getStringHeaders(), marshalledEntity.getMarshalledBytes(), keys.getPublic());
        response.close();
    }

    /**
     * @tpTestDetails Basic verification test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBasicVerification() throws Exception {
        WebTarget target = client.target(generateURL("/signed"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setDomain("samplezone.org");
        contentSignature.setSelector("test");
        contentSignature.setPrivateKey(keys.getPrivate());
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Manual verification test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testManualVerification() throws Exception {
        WebTarget target = client.target(generateURL("/signed/verify-manual"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setDomain("samplezone.org");
        contentSignature.setSelector("test");
        contentSignature.setAttribute("code", "hello");
        contentSignature.setPrivateKey(keys.getPrivate());
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Basic verification test with repository
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBasicVerificationRepository() throws Exception {
        WebTarget target = client.target(generateURL("/signed"));
        target.property(KeyRepository.class.getName(), repository);
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test");
        contentSignature.setDomain("samplezone.org");
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Basic verification test with bad signature
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBasicVerificationBadSignature() throws Exception {
        WebTarget target = client.target(generateURL("/signed"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test");
        contentSignature.setDomain("samplezone.org");
        contentSignature.setPrivateKey(badKey);
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Basic verification test with no signature
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBasicVerificationNoSignature() throws Exception {
        WebTarget target = client.target(generateURL("/signed"));
        Response response = target.request().post(Entity.text("hello world"));
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test for timestamp signature with set domain
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimestampSignature() throws Exception {
        DKIMSignature signature = new DKIMSignature();
        signature.setTimestamp();
        signature.setSelector("test");
        signature.setDomain("samplezone.org");
        signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());
        String sig = signature.toString();
        logger.info(DKIMSignature.DKIM_SIGNATURE + ": " + sig);
        new DKIMSignature(sig);
    }

    /**
     * @tpTestDetails Test for timestamp signature without set domain
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimestamp() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        verification.setStaleCheck(true);
        verification.setStaleSeconds(100);
        WebTarget target = client.target(generateURL("/signed/stamped"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        try {
            response.readEntity(String.class);
        } catch (Exception e) {
            throw e;
        }
        response.close();
    }

    /**
     * @tpTestDetails Stale timestamp test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStaleTimestamp() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        verification.setStaleCheck(true);
        verification.setStaleSeconds(1);
        WebTarget target = client.target(generateURL("/signed/stamped"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Thread.sleep(1500);
        try {
            response.readEntity(String.class);
            Assertions.fail("Validation error excepted.");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            MatcherAssert.assertThat("Unexcepted error", e.getMessage(),
                    containsString("Failed to verify signatures:\r\n"));
            MatcherAssert.assertThat("Unexcepted error", e.getMessage(), containsString("Signature is stale"));
        }
        response.close();
    }

    /**
     * @tpTestDetails Hour expiration test (expires attribute in Signed annotation in REST end-point is used).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExpiresHour() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/expires-hour"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.readEntity(String.class);
        response.close();
    }

    /**
     * @tpTestDetails Minute expiration test (expires attribute in Signed annotation in REST end-point is used).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExpiresMinutes() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/expires-minute"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.readEntity(String.class);
        response.close();
    }

    /**
     * @tpTestDetails Day expiration test (expires attribute in Signed annotation in REST end-point is used).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExpiresDays() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/expires-day"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.readEntity(String.class);
        response.close();
    }

    /**
     * @tpTestDetails Month expiration test (expires attribute in Signed annotation in REST end-point is used).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExpiresMonths() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/expires-month"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.readEntity(String.class);
        response.close();
    }

    /**
     * @tpTestDetails Year expiration test (expires attribute in Signed annotation in REST end-point is used).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExpiresYears() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/expires-year"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.readEntity(String.class);
        response.close();
    }

    /**
     * @tpTestDetails Fail expiration test (expires attribute in Signed annotation in REST end-point is used).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExpiresFail() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/expires-short"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Thread.sleep(1500);
        try {
            response.readEntity(String.class);
            throw new Exception("Signing error excepted");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            MatcherAssert.assertThat("Unexcepted error", e.getMessage(),
                    containsString("Failed to verify signatures:\r\n"));
            MatcherAssert.assertThat("Unexcepted error", e.getMessage(), containsString("Signature expired"));
        }
        response.close();
    }

    /**
     * @tpTestDetails Manual fail test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testManualFail() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.genKeyPair();

        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setKey(keyPair.getPublic());
        WebTarget target = client.target(generateURL("/signed/manual"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertNotNull(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE),
                "DKIM_SIGNATURE header is missing");
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        try {
            response.readEntity(String.class);
            throw new Exception("unreachable!");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            logger.info("UnauthorizedSignatureException message: " + e.getMessage());
            MatcherAssert.assertThat("Unexcepted error", e.getMessage(),
                    containsString("Failed to verify signatures:\r\n"));
            MatcherAssert.assertThat("Unexcepted error", e.getMessage(), containsString("Failed to verify signature."));
        }
        response.close();
    }

    /**
     * @tpTestDetails Manual success test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testManual() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/manual"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertNotNull(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE),
                "Missing header DKIM_SIGNATURE");
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String output = response.readEntity(String.class);
        Assertions.assertEquals("hello", output, RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Manual test with header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testManualWithHeader() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        WebTarget target = client.target(generateURL("/signed/header"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        logger.info(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertNotNull(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE), "Missing header DKIM_SIGNATURE");
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String output = response.readEntity(String.class);
        Assertions.assertEquals("hello world", output, RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Bad signature test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadSignature() throws Exception {
        WebTarget target = client.target(generateURL("/signed/bad-signature"));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        Assertions.assertNotNull(signatureHeader, "Missing header DKIM_SIGNATURE");
        logger.info(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);

        MarshalledEntity<String> entity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });

        try {
            contentSignature.verify(response.getStringHeaders(), entity.getMarshalledBytes(), keys.getPublic());
            Assertions.fail("Signing error excepted");
        } catch (SignatureException e) {
            logger.info("SignatureException message: " + e.getMessage());
        }
        response.close();
    }

    /**
     * @tpTestDetails Bad hash test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadHash() throws Exception {
        WebTarget target = client.target(generateURL("/signed/bad-hash"));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        Assertions.assertNotNull(signatureHeader);
        logger.info(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);

        MarshalledEntity<String> entity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });

        try {
            contentSignature.verify(response.getStringHeaders(), entity.getMarshalledBytes(), keys.getPublic());
            Assertions.fail("Signing error excepted");
        } catch (SignatureException e) {
            logger.info("SignatureException message: " + e.getMessage());
        }
        response.close();
    }

    /**
     * @tpTestDetails Proxy test with correct signature
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/"));
        target.property(KeyRepository.class.getName(), repository);
        SigningProxy proxy = target.proxy(SigningProxy.class);
        proxy.hello();
        proxy.postSimple("hello world");
    }

    /**
     * @tpTestDetails Proxy test with bad signature
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadSignatureProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/"));
        target.property(KeyRepository.class.getName(), repository);
        SigningProxy proxy = target.proxy(SigningProxy.class);
        try {
            proxy.bad();
            Assertions.fail("Signing error excepted");
        } catch (ResponseProcessingException e) {
            logger.info("ResponseProcessingException cause: " + e.getCause().getClass().getName());
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1169
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testBasicVerificationBadSignatureNoBody() throws Exception {
        WebTarget target = client.target(generateURL("/signed/nobody"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test");
        contentSignature.setDomain("samplezone.org");
        contentSignature.setPrivateKey(badKey);
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature).get();
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        response.close();
    }
}
