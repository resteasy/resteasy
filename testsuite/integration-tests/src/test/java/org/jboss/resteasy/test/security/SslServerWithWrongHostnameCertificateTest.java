package org.jboss.resteasy.test.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.setup.SnapshotServerSetupTask;
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
 * @tpTestCaseDetails Tests for SSL - server secured with certificate with wrong hostname "abc"
 * @tpSince RESTEasy 3.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(SslServerWithWrongHostnameCertificateTest.SslServerSetupTask.class)
public class SslServerWithWrongHostnameCertificateTest extends SslTestBase {

    public static class SslServerSetupTask extends SnapshotServerSetupTask {
        @Override
        protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
            SslTestBase.secureServer(client.getControllerClient(), SERVER_KEYSTORE_PATH);
        }
    }

    private static final Logger LOG = Logger.getLogger(SslServerWithWrongHostnameCertificateTest.class.getName());

    private static KeyStore truststore;

    private static final String SERVER_KEYSTORE_PATH = RESOURCES + "/server-wrong-hostname.keystore";
    private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client-wrong-hostname.truststore";
    private static final String URL = generateHttpsURL();

    @Deployment(name = DEPLOYMENT_NAME)
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
     * @tpTestDetails HostnameVerificationPolicy.STRICT test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate but server hostname(localhost) is not
     *                included among 'subject alternative names' in the certificate.
     *                HostnameVerificationPolicy is set to STRICT so exception should be thrown.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testHostnameVerificationPolicyStrict() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

                    resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.STRICT);

                    client = resteasyClientBuilder.trustStore(truststore).build();
                    client.target(URL).request().get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails HostnameVerificationPolicy.WILDCARD test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate but server hostname(localhost) is not
     *                included among 'subject alternative names' in the certificate.
     *                HostnameVerificationPolicy is set to WILDCARD so exception should be thrown.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testHostnameVerificationPolicyWildcard() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

                    resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.WILDCARD);

                    client = resteasyClientBuilder.trustStore(truststore).build();
                    client.target(URL).request().get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails HostnameVerificationPolicy.ANY test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate and server hostname(localhost) is not
     *                included among 'subject alternative names' in the certificate.
     *                Client should trust the server because HostnameVerificationPolicy is set to ANY.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testHostnameVerificationPolicyAny() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY);

        client = resteasyClientBuilder.trustStore(truststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails custom hostnameVerifier
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate and server hostname(localhost) is not
     *                included among 'subject alternative names' in the certificate.
     *                Instead it was generated for hostname "abc".
     *                Client should trust the server because custom HostnameVerifier is configured to return true for localhost.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testCustomHostnameVerifier() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        HostnameVerifier hostnameVerifier = (s, sslSession) -> s.equals(HOSTNAME);
        resteasyClientBuilder.hostnameVerifier(hostnameVerifier);

        client = resteasyClientBuilder.trustStore(truststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails custom hostnameVerifier - accept all
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate and server actual hostname(localhost) is
     *                not included among 'subject alternative names' in the certificate.
     *                Client should trust the server because HostnameVerifier acceptAll is configured to return true every time.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testCustomHostnameVerifierAcceptAll() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        HostnameVerifier acceptAll = (hostname, session) -> true;
        resteasyClientBuilder.hostnameVerifier(acceptAll);

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
