package org.jboss.resteasy.test.crypto;

import java.security.SecurityPermission;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.test.crypto.resource.PKCS7SignatureSmokeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Crypto
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for response secured by PKCS7SignatureInput
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PKCS7SignatureSmokeTest {
    protected static final Logger logger = Logger.getLogger(PKCS7SignatureSmokeTest.class.getName());
    static Client client;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PKCS7SignatureSmokeTest.class.getSimpleName());
        List<Class<?>> singletons = new ArrayList<>(1);
        singletons.add(PKCS7SignatureSmokeResource.class);
        // This can be removed once RESTEASY-3344 is resolved and the WildFly upgrade (WFLY-18231) is done
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new SecurityPermission("removeProviderProperty.BC"),
                new SecurityPermission("putProviderProperty.BC")), "permissions.xml");
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
        WebTarget target = client.target(generateURL());
        String data = target.path("test/signed/text").request().get(String.class);
        logger.info(data);
    }

    /**
     * @tpTestDetails Get decoded data
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void decodedData() throws Exception {
        WebTarget target = client.target(generateURL());
        target = target.path("test/signed/pkcs7-signature");
        PKCS7SignatureInput signed = target.request().get(PKCS7SignatureInput.class);
        @SuppressWarnings(value = "unchecked")
        String output = (String) signed.getEntity(String.class, MediaType.TEXT_PLAIN_TYPE);
        logger.info(output);
        Assertions.assertEquals("hello world", output, "Wrong content of response");
    }
}
