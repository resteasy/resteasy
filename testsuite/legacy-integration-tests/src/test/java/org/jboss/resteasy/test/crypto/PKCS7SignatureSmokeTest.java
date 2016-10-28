package org.jboss.resteasy.test.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.test.crypto.resource.PKCS7SignatureSmokeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @tpSubChapter Crypto
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for response secured by PKCS7SignatureInput
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PKCS7SignatureSmokeTest {
    protected static final Logger logger = LogManager.getLogger(PKCS7SignatureSmokeTest.class.getName());
    static Client client;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PKCS7SignatureSmokeTest.class.getSimpleName());
        List<Class<?>> singletons = new ArrayList<>(1);
        singletons.add(PKCS7SignatureSmokeResource.class);
        return TestUtil.finishContainerPrepare(war, null, singletons, (Class<?>[]) null);
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(PKCS7SignatureSmokeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Get encoded data
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void encodedData() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL());
        String data = target.path("test/signed/text").request().get(String.class);
        logger.info(data);
        client.close();
    }

    /**
     * @tpTestDetails Get decoded data
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void decodedData() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL());
        target = target.path("test/signed/pkcs7-signature");
        PKCS7SignatureInput signed = target.request().get(PKCS7SignatureInput.class);
        @SuppressWarnings(value = "unchecked")
        String output = (String) signed.getEntity(String.class, MediaType.TEXT_PLAIN_TYPE);
        logger.info(output);
        Assert.assertEquals("Wrong content of response", "hello world", output);
    }
}

