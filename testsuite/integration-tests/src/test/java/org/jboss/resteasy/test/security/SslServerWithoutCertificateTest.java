package org.jboss.resteasy.test.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.security.resource.SslResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for SSL - server without certificate
 * @tpSince RESTEasy 3.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SslServerWithoutCertificateTest extends SslTestBase {

    private static final Logger LOG = Logger.getLogger(SslServerWithoutCertificateTest.class.getName());

    private static KeyStore truststore;

    private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client.truststore";
    private static final String URL = generateHttpsURL(false);

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_NAME);
        return TestUtil.finishContainerPrepare(war, null, SslResource.class);
    }

    @BeforeAll
    public static void prepareTruststore()
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        truststore = KeyStore.getInstance("jks");
        try (InputStream in = new FileInputStream(CLIENT_TRUSTSTORE_PATH)) {
            truststore.load(in, PASSWORD.toCharArray());
        }
    }

    /**
     * @tpTestDetails
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is not secured at all. Client should not trust the unsecured server.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testServerWithoutCertificate() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

                    client = resteasyClientBuilder.trustStore(truststore).build();
                    client.target(URL).request().get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is not secured at all. However, disableTrustManager is used so client should trust this
     *                server.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testServerWithoutCertificateDisabledTrustManager() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder = resteasyClientBuilder.disableTrustManager();

        client = resteasyClientBuilder.trustStore(truststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    @AfterEach
    public void after() {
        client.close();
    }

}
