package org.jboss.resteasy.test.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.security.doseta.UnauthorizedSignatureException;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.test.crypto.resource.SigningResource;
import org.jboss.resteasy.test.crypto.resource.SigningProxy;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.lang.reflect.ReflectPermission;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @tpSubChapter Crypto
 * @tpChapter Integration tests
 * @tpTestCaseDetails Signing test for RESTEasy-crypto
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SigningTest {
    public static KeyPair keys;
    public static DosetaKeyRepository repository;
    public static PrivateKey badKey;
    private static ResteasyClient client;

    protected final Logger logger = LogManager.getLogger(PKCS7SignatureSmokeTest.class.getName());

    private static final String RESPONSE_ERROR_MSG = "Response contains wrong content";
    static final String testJksPath;

    static {
        testJksPath = TestUtil.getResourcePath(SigningTest.class, "SigningTest.jks");
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SigningTest.class.getSimpleName());
        war.addClass(SigningProxy.class);
        war.addAsResource(SigningTest.class.getPackage(), "SigningTest.jks", "test.jks");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")
        ), "permissions.xml");
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.doseta.keystore.classpath", "test.jks");
        contextParams.put("resteasy.doseta.keystore.password", "password");
        contextParams.put("resteasy.context.objects", "org.jboss.resteasy.security.doseta.KeyRepository : org.jboss.resteasy.security.doseta.ConfiguredDosetaKeyRepository");
        contextParams.put("resteasy.doseta.use.dns", "false");
        return TestUtil.finishContainerPrepare(war, contextParams, SigningResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SigningTest.class.getSimpleName());
    }

    @BeforeClass
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

    @AfterClass
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

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        MarshalledEntity<String> marshalledEntity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });
        Assert.assertEquals(RESPONSE_ERROR_MSG, "hello world", marshalledEntity.getEntity());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        logger.info(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        Assert.assertNotNull("Missing DKIM_SIGNATURE header", signatureHeader);

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
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Thread.sleep(1500);
        try {
            response.readEntity(String.class);
            Assert.fail("Validation error excepted.");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            Assert.assertThat("Unexcepted error", e.getMessage(), containsString("Failed to verify signatures:\r\n"));
            Assert.assertThat("Unexcepted error", e.getMessage(), containsString("Signature is stale"));
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Thread.sleep(1500);
        try {
            response.readEntity(String.class);
            throw new Exception("Signing error excepted");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            Assert.assertThat("Unexcepted error", e.getMessage(), containsString("Failed to verify signatures:\r\n"));
            Assert.assertThat("Unexcepted error", e.getMessage(), containsString("Signature expired"));
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
        Assert.assertNotNull("DKIM_SIGNATURE header is missing", response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        try {
            response.readEntity(String.class);
            throw new Exception("unreachable!");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            logger.info("UnauthorizedSignatureException message: " + e.getMessage());
            Assert.assertThat("Unexcepted error", e.getMessage(), containsString("Failed to verify signatures:\r\n"));
            Assert.assertThat("Unexcepted error", e.getMessage(), containsString("Failed to verify signature."));
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
        Assert.assertNotNull("Missing header DKIM_SIGNATURE", response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String output = response.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "hello", output);
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
        Assert.assertNotNull("Missing header DKIM_SIGNATURE", response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String output = response.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "hello world", output);
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        Assert.assertNotNull("Missing header DKIM_SIGNATURE", signatureHeader);
        logger.info(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);

        MarshalledEntity<String> entity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });

        try {
            contentSignature.verify(response.getStringHeaders(), entity.getMarshalledBytes(), keys.getPublic());
            Assert.fail("Signing error excepted");
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        Assert.assertNotNull(signatureHeader);
        logger.info(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);

        MarshalledEntity<String> entity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });

        try {
            contentSignature.verify(response.getStringHeaders(), entity.getMarshalledBytes(), keys.getPublic());
            Assert.fail("Signing error excepted");
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
            Assert.fail("Signing error excepted");
        } catch (ResponseProcessingException e) {
            logger.info("ResponseProcessingException cause: " + e.getCause().getClass().getName());
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1169
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testBasicVerificationBadSignatureNoBody() throws Exception {
        WebTarget target = client.target(generateURL("/signed/nobody"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test");
        contentSignature.setDomain("samplezone.org");
        contentSignature.setPrivateKey(badKey);
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature).get();
        Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        response.close();
    }
}
