package org.jboss.resteasy.test.security;

import static org.jboss.resteasy.test.ContainerConstants.SSL_CONTAINER_PORT_OFFSET_WILDCARD;
import static org.jboss.resteasy.test.ContainerConstants.SSL_CONTAINER_QUALIFIER_WILDCARD;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for SSL - server secured with certificate with wildcard hostname "*host"
 * @tpSince RESTEasy 3.7.0
 */
@Disabled("RESTEASY-3451")
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SslServerWithWildcardHostnameCertificateTest extends SslTestBase {

    private static final Logger LOG = Logger.getLogger(SslServerWithWildcardHostnameCertificateTest.class.getName());

    private static KeyStore truststore;

    private static final String SERVER_KEYSTORE_PATH = RESOURCES + "/server-wildcard-hostname.keystore";
    private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client-wildcard-hostname.truststore";
    private static final String URL = generateHttpsURL(SSL_CONTAINER_PORT_OFFSET_WILDCARD);

    @TargetsContainer(SSL_CONTAINER_QUALIFIER_WILDCARD)
    @Deployment(managed = false, name = DEPLOYMENT_NAME)
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

    @BeforeEach
    public void startContainer() throws Exception {
        if (!containerController.isStarted(SSL_CONTAINER_QUALIFIER_WILDCARD)) {
            containerController.start(SSL_CONTAINER_QUALIFIER_WILDCARD);
            secureServer(SERVER_KEYSTORE_PATH, SSL_CONTAINER_PORT_OFFSET_WILDCARD);
            deployer.deploy(DEPLOYMENT_NAME);
        }
    }

    /**
     * @tpTestDetails HostnameVerificationPolicy.WILDCARD test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate, but only wildcard of server hostname
     *                (*host) is included among 'subject alternative names' in the certificate.
     *                Client should trust the server because HostnameVerificationPolicy is set to WILDCARD.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testHostnameVerificationPolicyWildcard() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.WILDCARD);

        client = resteasyClientBuilder.trustStore(truststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails HostnameVerificationPolicy.STRICT test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate, but only wildcard of server hostname
     *                (*host) is included among 'subject alternative names' in the certificate.
     *                HostnameVerificationPolicy is set to STRICT so exception should be thrown.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testHostnameVerificationPolicyStrict() throws Exception {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.STRICT);

        client = resteasyClientBuilder.trustStore(truststore).build();
        try {
            if (InetAddress.getByName("localhost.localdomain") != null) {
                String anotherURL = URL.replace("localhost", "localhost.localdomain");
                try {
                    client.target(anotherURL).request().get();
                    Assertions.fail("ProcessingException ie expected");
                } catch (ProcessingException e) {
                    //expected
                }
            }
        } catch (UnknownHostException e) {
            try {
                if (InetAddress.getByName("localhost.localhost") != null) {
                    String anotherURL = URL.replace("localhost", "localhost.localhost");
                    try {
                        client.target(anotherURL).request().get();
                        Assertions.fail("ProcessingException ie expected");
                    } catch (ProcessingException e1) {
                        //expected
                    }
                }
            } catch (UnknownHostException e2) {
                LOG.warn("Neither 'localhost.localdomain' nor 'local.localhost'can be resolved, "
                        + "nothing is checked");
            }
        }
    }

    @AfterEach
    public void after() {
        client.close();
    }

}
