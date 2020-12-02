package org.jboss.resteasy.test.security;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.security.resource.SslResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

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

import static org.jboss.resteasy.test.ContainerConstants.SSL_CONTAINER_PORT_OFFSET_SNI;
import static org.jboss.resteasy.test.ContainerConstants.SSL_CONTAINER_QUALIFIER_SNI;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for sniHostNames - method to choose which certificate should be presented by the server
 * @tpSince RESTEasy 3.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SslSniHostNamesTest extends SslTestBase {

   private static final Logger LOG = Logger.getLogger(SslSniHostNamesTest.class.getName());

   private static KeyStore truststore;

   private static String BATCH = RESOURCES + "/ssl-batch-command.txt";
   private static String SERVER_WRONG_KEYSTORE_PATH = RESOURCES + "/server-wrong-hostname.keystore";
   private static String SERVER_TRUSTED_KEYSTORE_PATH = RESOURCES + "/server.keystore";

   private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "/client.truststore";
   private static final String URL = generateHttpsURL(SSL_CONTAINER_PORT_OFFSET_SNI);

   @TargetsContainer(SSL_CONTAINER_QUALIFIER_SNI)
   @Deployment(managed=false, name=DEPLOYMENT_NAME)
   public static Archive<?> createDeployment() {
      WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_NAME);
      return TestUtil.finishContainerPrepare(war, null, SslResource.class);
   }

   @BeforeClass
   public static void prepareTruststore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
      truststore = KeyStore.getInstance("jks");
      try (InputStream in = new FileInputStream(CLIENT_TRUSTSTORE_PATH)) {
         truststore.load(in, PASSWORD.toCharArray());
      }
   }

   @Before
   public void startContainer() throws Exception {
      if (!containerController.isStarted(SSL_CONTAINER_QUALIFIER_SNI)) {
         containerController.start(SSL_CONTAINER_QUALIFIER_SNI);
         secureServer();
         deployer.deploy(DEPLOYMENT_NAME);
      }
   }

   /**
    * @tpTestDetails Client has truststore containing self-signed certificate.
    * Server/endpoint has two certificates - managed by two separate SSLContexts. Default SSLContext has wrong certificate - not trusted by client.
    * @tpSince RESTEasy 3.7.0
    */
   @Test(expected = ProcessingException.class)
   public void testException() {
      resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
      resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

      client = resteasyClientBuilder.trustStore(truststore).build();
      client.target(URL).request().get();
   }

   /**
    * @tpTestDetails Client has truststore containing self-signed certificate.
    * Server/endpoint has two certificates - managed by two separate SSLContexts. Default SSLContext has wrong certificate - not trusted by client.
    * However, client requests certificate for localhost using sniHostNames method.
    * @tpSince RESTEasy 3.7.0
    */
   @Test
   public void test() {
      resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
      resteasyClientBuilder.setIsTrustSelfSignedCertificates(false);

      resteasyClientBuilder.sniHostNames(HOSTNAME);

      client = resteasyClientBuilder.trustStore(truststore).build();
      Response response = client.target(URL).request().get();
      Assert.assertEquals("Hello World!", response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
   }

   /**
    * Set up ssl in jboss-cli so https endpoint can be accessed only if client trusts certificates in the server keystore.
    * @throws Exception
    */
   private static void secureServer() throws Exception {
      File file = new File(SERVER_WRONG_KEYSTORE_PATH);
      SERVER_WRONG_KEYSTORE_PATH = file.getAbsolutePath();

      file = new File(SERVER_TRUSTED_KEYSTORE_PATH);
      SERVER_TRUSTED_KEYSTORE_PATH = file.getAbsolutePath();

      file = new File(BATCH);
      BATCH = file.getAbsolutePath();

      if (TestUtil.isWindows()) {
         SERVER_WRONG_KEYSTORE_PATH = SERVER_WRONG_KEYSTORE_PATH.replace("\\","\\\\");
         SERVER_TRUSTED_KEYSTORE_PATH = SERVER_TRUSTED_KEYSTORE_PATH.replace("\\","\\\\");
         BATCH = BATCH.replace("\\","\\\\");
      }

      OnlineManagementClient client = TestUtil.clientInit(SSL_CONTAINER_PORT_OFFSET_SNI);

      // create SSLContext with untrusted certificate (hostname is wrong)
      TestUtil.runCmd(client, String.format("/subsystem=elytron/key-store=httpsKS:add(path=%s,credential-reference={clear-text=%s},type=JKS)", SERVER_WRONG_KEYSTORE_PATH, PASSWORD));
      TestUtil.runCmd(client, String.format("/subsystem=elytron/key-manager=httpsKM:add(key-store=httpsKS,credential-reference={clear-text=%s})", PASSWORD));
      if (TestUtil.isIbmJdk()) { // on ibm java, client doesn't use TLSv1.2
         TestUtil.runCmd(client, "/subsystem=elytron/server-ssl-context=httpsSSC:add(key-manager=httpsKM,protocols=[\"TLSv1\"])");
      } else {
         TestUtil.runCmd(client, "/subsystem=elytron/server-ssl-context=httpsSSC:add(key-manager=httpsKM,protocols=[\"TLSv1.2\"])");
      }


      // create SSLContext with trusted certificate
      TestUtil.runCmd(client, String.format("/subsystem=elytron/key-store=httpsKS1:add(path=%s,credential-reference={clear-text=%s},type=JKS)", SERVER_TRUSTED_KEYSTORE_PATH, PASSWORD));
      TestUtil.runCmd(client, String.format("/subsystem=elytron/key-manager=httpsKM1:add(key-store=httpsKS1,credential-reference={clear-text=%s})", PASSWORD));
      if (TestUtil.isIbmJdk()) {
         TestUtil.runCmd(client, "/subsystem=elytron/server-ssl-context=httpsSSC1:add(key-manager=httpsKM1,protocols=[\"TLSv1\"])");
      } else {
         TestUtil.runCmd(client, "/subsystem=elytron/server-ssl-context=httpsSSC1:add(key-manager=httpsKM1,protocols=[\"TLSv1.2\"])");
      }


      // set untrusted SSLContext as default and trusted SSLContext to be activated with sniHostNames("localhost")
      TestUtil.runCmd(client, "/subsystem=elytron/server-ssl-sni-context=test-sni:add(default-ssl-context=httpsSSC,host-context-map={localhost=httpsSSC1})");

      // remove the reference to the legacy security realm and use configuration above instead
      TestUtil.runCmd(client, String.format("run-batch --file=%s", BATCH));

      Administration admin = new Administration(client, 240);
      admin.reload();

      client.close();
   }

   @After
   public void after() {
      client.close();
   }

}
