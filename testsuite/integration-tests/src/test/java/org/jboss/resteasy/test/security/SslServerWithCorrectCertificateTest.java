package org.jboss.resteasy.test.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.setup.SnapshotServerSetupTask;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.security.resource.CustomTrustManager;
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
 * @tpTestCaseDetails Tests for SSL - server secured with correct certificate for "localhost"
 * @tpSince RESTEasy 3.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(SslServerWithCorrectCertificateTest.SslServerSetupTask.class)
public class SslServerWithCorrectCertificateTest extends SslTestBase {

    public static class SslServerSetupTask extends SnapshotServerSetupTask {
        @Override
        protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
            SslTestBase.secureServer(client.getControllerClient(), SERVER_KEYSTORE_PATH);
        }
    }

    private static final Logger LOG = Logger.getLogger(SslServerWithCorrectCertificateTest.class.getName());

    private static KeyStore correctTruststore;
    private static KeyStore differentTruststore;

    private static final String SERVER_KEYSTORE_PATH = RESOURCES + "/server.keystore";
    private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client.truststore";
    private static final String DIFFERENT_CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client-different-cert.truststore";
    private static final String URL = generateHttpsURL();

    @Deployment(name = DEPLOYMENT_NAME)
    public static Archive<?> createDeployment() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_NAME);
        return TestUtil.finishContainerPrepare(war, null, SslResource.class);
    }

    @BeforeAll
    public static void prepareTruststores()
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        correctTruststore = KeyStore.getInstance("jks");
        try (InputStream in = new FileInputStream(CLIENT_TRUSTSTORE_PATH)) {
            correctTruststore.load(in, PASSWORD.toCharArray());
        }

        differentTruststore = KeyStore.getInstance("jks");
        try (InputStream in = new FileInputStream(DIFFERENT_CLIENT_TRUSTSTORE_PATH)) {
            differentTruststore.load(in, PASSWORD.toCharArray());
        }
    }

    /**
     * @tpTestDetails Trusted server
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testTrustedServer() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        client = resteasyClientBuilder.trustStore(correctTruststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails Untrusted server
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with different self-signed certificate so exception should be thrown.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testUntrustedServer() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

                    client = resteasyClientBuilder.trustStore(differentTruststore).build();
                    client.target(URL).request().get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails Client with no truststore
     *                Server/endpoint is secured with self-signed certificate.
     *                Client has no truststore so it does not trust the server.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testClientWithoutTruststore() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

                    client = resteasyClientBuilder.build();
                    client.target(URL).request().get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails Custom SSLContext
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testCustomSSLContext() throws Exception {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new CustomTrustManager(correctTruststore) }, null);

        client = resteasyClientBuilder.sslContext(sslContext).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails HostnameVerificationPolicy.STRICT test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with the same self-signed certificate and server hostname (localhost) is
     *                included among 'subject alternative names' in the certificate.
     *                HostnameVerificationPolicy is set to STRICT.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testHostnameVerificationPolicyStrict() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.STRICT);

        client = resteasyClientBuilder.trustStore(correctTruststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails different cert + HostnameVerificationPolicy.ANY test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with different self-signed certificate so exception should be thrown.
     *                HostnameVerificationPolicy is set to ANY but it doesn't matter when certificates doesn't match.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testHostnameVerificationPolicyAny() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

                    resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY);

                    client = resteasyClientBuilder.trustStore(differentTruststore).build();
                    client.target(URL).request().get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails disableTrustManager() test
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with different self-signed certificate.
     *                However, disableTrustManager is used so client should trust this certificate (and all others).
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testDisableTrustManager() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder = resteasyClientBuilder.disableTrustManager();

        client = resteasyClientBuilder.trustStore(differentTruststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails Test for https://issues.jboss.org/browse/RESTEASY-2065
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with different self-signed certificate, but by default, all self-signed
     *                certificates should be trusted.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testIsTrustSelfSignedCertificatesDefault() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();

        client = resteasyClientBuilder.trustStore(differentTruststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    /**
     * @tpTestDetails Test for https://issues.jboss.org/browse/RESTEASY-2065
     *                Client has truststore containing self-signed certificate.
     *                Server/endpoint is secured with different self-signed certificate, but after
     *                setIsTrustSelfSignedCertificates(true), all self-signed certificates should be trusted.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testIsTrustSelfSignedCertificatesTrue() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(true);

        client = resteasyClientBuilder.trustStore(differentTruststore).build();
        Response response = client.target(URL).request().get();
        Assertions.assertEquals("Hello World!", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void testTrustedServerWithClientConfigProvider() throws IOException {
        String jarPath = ClientConfigProviderTestJarHelper.createClientConfigProviderTestJarWithSSL();
        File clientTruststore = new File(CLIENT_TRUSTSTORE_PATH);
        Process process = ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_SSLCONTEXT_USED,
                jarPath,
                new String[] { URL, clientTruststore.getAbsolutePath() });
        String line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        // first request will succeed because SSLContext from ClientConfigProvider will be used. Second request will fail because user will set sslContext on RestEasyBuilder to SSLContext.getDefault()
        Assertions.assertEquals("200", line);
        process.destroy();

        process = ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_CLIENTCONFIG_SSLCONTEXT_IGNORED_WHEN_DIFFERENT_SET,
                jarPath,
                new String[] { URL, clientTruststore.getAbsolutePath() });
        line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assertions.assertEquals("SSLHandshakeException", line);
        process.destroy();
        Assertions.assertTrue(new File(jarPath).delete());
    }

    @AfterEach
    public void after() {
        if (client != null) {
            client.close();
        }
    }

}
