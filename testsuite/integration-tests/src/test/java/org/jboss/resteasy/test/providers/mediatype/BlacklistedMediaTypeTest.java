package org.jboss.resteasy.test.providers.mediatype;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.function.Function;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.security.KeyTools;
import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.test.crypto.resource.CryptoCertResource;
import org.jboss.resteasy.test.providers.mediatype.resource.BlacklistedMediaTypeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Blacklisted media types - RESTEASY-2198
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BlacklistedMediaTypeTest {

    private static final String APPLICATION_SIGNED_EXCHANGE = "application/signed-exchange";
    private static final String APPLICATION_SIGNED_EXCHANGE_TEXT_PLAIN = "application/signed-exchange;q=1.0, text/plain;q=0.9";
    private static final String COULD_NOT_FIND_MESSAGE_BODY_WRITER = "Could not find MessageBodyWriter for response object of type: ";

    private static X509Certificate cert;
    private static PrivateKey privateKey;
    private static Client client;

    private static final Function<Response, String> READ_STRING_ENTITY = (Response r) -> r.readEntity(String.class);
    private static final Function<Response, String> READ_ENVELOPED_INPUT_ENTITY = (Response r) -> {
        @SuppressWarnings("unchecked")
        EnvelopedInput<String> input = r.readEntity(EnvelopedInput.class);
        String entity = input.getEntity(String.class, privateKey, cert);
        return entity;
    };

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(BlacklistedMediaTypeTest.class.getSimpleName());
        try {
            // Code borrowed from org.jboss.resteasy.test.crypto.CryptoTest
            BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
            Security.addProvider(bouncyCastleProvider);
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
            privateKey = keyPair.getPrivate();
            cert = KeyTools.generateTestCertificate(keyPair);
            String privateKeyString = toString(privateKey);
            String certString = toString(cert);
            war.addAsResource(new StringAsset(privateKeyString), "privateKey.txt");
            war.addAsResource(new StringAsset(certString), "cert.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        war.addAsManifestResource("jboss-deployment-structure-bouncycastle.xml", "jboss-deployment-structure.xml");
        war.addAsWebInfResource(BlacklistedMediaTypeTest.class.getPackage(), "BlacklistedMediaTypeFile1",
                "classes/BlacklistedMediaTypeFile1");
        war.addAsWebInfResource(BlacklistedMediaTypeTest.class.getPackage(), "BlacklistedMediaTypeFile2",
                "classes/BlacklistedMediaTypeFile2");
        return TestUtil.finishContainerPrepare(war, null, BlacklistedMediaTypeResource.class, CryptoCertResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, BlacklistedMediaTypeTest.class.getSimpleName());
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Test ByteArrayProvider
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testByteArrayProvider() {
        doTest("/byteArrayProvider");
    }

    /**
     * @tpTestDetails Test DataSourceProvider
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testDataSourceProvider() {
        doTest("/dataSourceProvider");
    }

    /**
     * @tpTestDetails Test EnvelopedWriter
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testEnvelopedWriter() throws Exception {
        doTest0("/envelopedWriter", READ_STRING_ENTITY, READ_ENVELOPED_INPUT_ENTITY);
    }

    /**
     * @tpTestDetails Test FileProvider
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testFileProvider() {
        doTest("/fileProvider");
    }

    /**
     * @tpTestDetails Test FileRangeWriter
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testFileRangeWriter() {
        doTest("/fileRangeWriter");
    }

    /**
     * @tpTestDetails Test InputStreamProvider
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testInputStreamProvider() {
        doTest("/inputStreamProvider");
    }

    /**
     * @tpTestDetails Test ReaderProvider
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testReaderProvider() {
        doTest("/readerProvider");
    }

    /**
     * @tpTestDetails Test StreamingOutputProvider
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testStreamingOutputProvider() {
        doTest("/streamingOutputProvider");
    }

    /**
     * @tpTestDetails Test StringTextStar
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testStringTextStar() {
        doTest("/stringTextStar");
    }

    void doTest(String path) {
        doTest0(path, READ_STRING_ENTITY, READ_STRING_ENTITY);
    }

    void doTest0(String path, Function<Response, String> f1, Function<Response, String> f2) {
        {
            Builder request = client.target(generateURL(path)).request();
            request.header("Accept", APPLICATION_SIGNED_EXCHANGE);
            Response response = request.get();
            Assertions.assertEquals(500, response.getStatus());
            String entity = f1.apply(response);
            Assertions.assertTrue(entity.startsWith(COULD_NOT_FIND_MESSAGE_BODY_WRITER),
                    "got: " + entity);
        }
        {
            Builder request = client.target(generateURL(path)).request();
            request.header("Accept", APPLICATION_SIGNED_EXCHANGE_TEXT_PLAIN);
            Response response = request.get();
            Assertions.assertEquals(200, response.getStatus());
            String entity = f2.apply(response);
            Assertions.assertEquals("hello", entity);
        }
    }

    /**
     * Write the object to a Base64 string.
     * Borrowed from org.jboss.resteasy.test.crypto.CryptoTest
     */
    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
