package org.jboss.resteasy.test.security;

import java.io.File;
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
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.setup.SnapshotServerSetupTask;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;
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
 * @tpTestCaseDetails Test for sniHostNames - method to choose which certificate should be presented by the server
 * @tpSince RESTEasy 3.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(SslSniHostNamesTest.SslServerSetupTask.class)
public class SslSniHostNamesTest extends SslTestBase {

    public static class SslServerSetupTask extends SnapshotServerSetupTask {
        @Override
        protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
            File file = new File(SERVER_WRONG_KEYSTORE_PATH);
            SERVER_WRONG_KEYSTORE_PATH = file.getAbsolutePath();

            file = new File(SERVER_TRUSTED_KEYSTORE_PATH);
            SERVER_TRUSTED_KEYSTORE_PATH = file.getAbsolutePath();

            if (TestUtil.isWindows()) {
                SERVER_WRONG_KEYSTORE_PATH = SERVER_WRONG_KEYSTORE_PATH.replace("\\", "\\\\");
                SERVER_TRUSTED_KEYSTORE_PATH = SERVER_TRUSTED_KEYSTORE_PATH.replace("\\", "\\\\");
            }

            final CompositeOperationBuilder builder = CompositeOperationBuilder.create();
            ModelNode credentialReference = new ModelNode().setEmptyObject();
            credentialReference.get("clear-text").set(PASSWORD);

            // create SSLContext with untrusted certificate (hostname is wrong)
            ModelNode address = Operations.createAddress("subsystem", "elytron", "key-store", "httpsKS");
            ModelNode op = Operations.createAddOperation(address);
            op.get("path").set(SERVER_WRONG_KEYSTORE_PATH);
            op.get("credential-reference").set(credentialReference);
            op.get("type").set("JKS");
            builder.addStep(op);

            address = Operations.createAddress("subsystem", "elytron", "key-manager", "httpsKM");
            op = Operations.createAddOperation(address);
            op.get("key-store").set("httpsKS");
            op.get("credential-reference").set(credentialReference);
            builder.addStep(op);

            final ModelNode protocols = new ModelNode().setEmptyList();
            if (TestUtil.isIbmJdk()) { // on ibm java, client doesn't use TLSv1.2
                protocols.add("TLSv1");
            } else {
                protocols.add("TLSv1.2");
            }
            address = Operations.createAddress("subsystem", "elytron", "server-ssl-context", "httpsSSC");
            op = Operations.createAddOperation(address);
            op.get("key-manager").set("httpsKM");
            op.get("protocols").set(protocols);
            builder.addStep(op);

            // create SSLContext with trusted certificate
            address = Operations.createAddress("subsystem", "elytron", "key-store", "httpsKS1");
            op = Operations.createAddOperation(address);
            op.get("path").set(SERVER_TRUSTED_KEYSTORE_PATH);
            op.get("credential-reference").set(credentialReference);
            op.get("type").set("JKS");
            builder.addStep(op);

            address = Operations.createAddress("subsystem", "elytron", "key-manager", "httpsKM1");
            op = Operations.createAddOperation(address);
            op.get("key-store").set("httpsKS1");
            op.get("credential-reference").set(credentialReference);
            builder.addStep(op);

            address = Operations.createAddress("subsystem", "elytron", "server-ssl-context", "httpsSSC1");
            op = Operations.createAddOperation(address);
            op.get("key-manager").set("httpsKM1");
            op.get("protocols").set(protocols);
            builder.addStep(op);

            // set untrusted SSLContext as default and trusted SSLContext to be activated with sniHostNames("localhost")
            address = Operations.createAddress("subsystem", "elytron", "server-ssl-sni-context", "test-sni");
            op = Operations.createAddOperation(address);
            op.get("default-ssl-context").set("httpsSSC");
            final ModelNode hostContextMap = op.get("host-context-map").setEmptyObject();
            hostContextMap.get("localhost").set("httpsSSC1");
            builder.addStep(op);

            // remove the reference to the legacy security realm and use configuration above instead
            address = Operations.createAddress("subsystem", "undertow", "server", "default-server", "https-listener", "https");
            builder.addStep(Operations.createUndefineAttributeOperation(address, "security-realm"));
            builder.addStep(Operations.createWriteAttributeOperation(address, "ssl-context", "test-sni"));

            final ModelNode result = client.getControllerClient().execute(builder.build());
            if (!Operations.isSuccessfulOutcome(result)) {
                throw new RuntimeException("Failed to configure SSL: " + Operations.getFailureDescription(result).asString());
            }
        }
    }

    private static KeyStore truststore;
    private static String SERVER_WRONG_KEYSTORE_PATH = RESOURCES + "/server-wrong-hostname.keystore";
    private static String SERVER_TRUSTED_KEYSTORE_PATH = RESOURCES + "/server.keystore";

    private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client.truststore";
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
     * @tpTestDetails Client has truststore containing self-signed certificate.
     *                Server/endpoint has two certificates - managed by two separate SSLContexts. Default SSLContext has wrong
     *                certificate - not trusted by client.
     * @tpSince RESTEasy 3.7.0
     */
    @Test()
    public void testException() {
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
     * @tpTestDetails Client has truststore containing self-signed certificate.
     *                Server/endpoint has two certificates - managed by two separate SSLContexts. Default SSLContext has wrong
     *                certificate - not trusted by client.
     *                However, client requests certificate for localhost using sniHostNames method.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void test() {
        resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

        resteasyClientBuilder.sniHostNames(HOSTNAME);

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
