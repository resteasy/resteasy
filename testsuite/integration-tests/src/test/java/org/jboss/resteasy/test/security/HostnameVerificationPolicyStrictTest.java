package org.jboss.resteasy.test.security;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.security.resource.HostnameVerificationPolicyResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.cert.CertificateException;


/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for HostnameVerificationPolicy - STRICT
 * @tpSince RESTEasy 3.6.3.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HostnameVerificationPolicyStrictTest {

   protected static final Logger LOG = Logger.getLogger(HostnameVerificationPolicyStrictTest.class.getName());

   private static Client client;
   private static ResteasyClientBuilder resteasyClientBuilder;
   private static KeyStore trustStore;

   private static String RESOURCES = "src/test/resources/org/jboss/resteasy/test/security/";
   private static String SERVER_KEYSTORE_PATH = RESOURCES + "server.keystore";
   private static final String CLIENT_TRUSTSTORE_PATH = RESOURCES + "client.truststore";
   private static final String GENERATE_CERTIFICATES_SCRIPT_UNIX = RESOURCES + "generate.key.sh";
   private static final String GENERATE_CERTIFICATES_SCRIPT_WINDOWS = RESOURCES + "generate.key.bat";
   private static final String PASSWORD = "123456";
   private static final String HOSTNAME = PortProviderUtil.getHost();


   @Deployment
   public static Archive<?> deployDefault() throws Exception {
      generateCertificates();
      secureServer();
      WebArchive war = TestUtil.prepareArchive(HostnameVerificationPolicyStrictTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, HostnameVerificationPolicyResource.class);
   }

   @BeforeClass
   public static void init() throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
      backupDefaultConfiguration();
      trustStore = KeyStore.getInstance("jks");
      try (InputStream in = new FileInputStream(CLIENT_TRUSTSTORE_PATH)) {
         trustStore.load(in, PASSWORD.toCharArray());
      }
      resteasyClientBuilder = new ResteasyClientBuilderImpl();
      resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.STRICT);
   }

   @AfterClass
   public static void after() throws Exception {
      resetConfiguration();
      client.close();
   }

   private String generateHttpsURL(String path) {
      return PortProviderUtil.generateHttpsURL(path, HostnameVerificationPolicyStrictTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Client has truststore containing self-signed certificate.
    * Server/endpoint is secured with the same self-signed certificate containing server actual host address among 'subject alternative names' in the certificate.
    * @tpSince RESTEasy 3.6.3.Final
    */
   @Test
   public void test() {
      client = resteasyClientBuilder.trustStore(trustStore).build();
      Response response = client.target(generateHttpsURL("/secure/hello")).request().get();
      Assert.assertEquals("Hello World!", response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
   }

   /**
    * Set up ssl in jboss-cli so https endpoint can be accessed only if client trusts certificates in the server keystore.
    * @throws Exception
    */
   private static void secureServer() throws Exception {
      File file = new File(SERVER_KEYSTORE_PATH);
      SERVER_KEYSTORE_PATH = file.getAbsolutePath();

      if (TestUtil.isWindows()) {
         SERVER_KEYSTORE_PATH = SERVER_KEYSTORE_PATH.replace("\\","\\\\");
      }

      OnlineManagementClient client = TestUtil.clientInit();

      TestUtil.runCmd(client,"/core-service=management/security-realm=ApplicationRealm/server-identity=ssl:remove(keystore-path, keystore-password, alias)");
      TestUtil.runCmd(client, String.format("/core-service=management/security-realm=ApplicationRealm/server-identity=ssl:add(keystore-path=%s, keystore-password=%s, alias=\"server\")", SERVER_KEYSTORE_PATH, PASSWORD));

      // above changes request reload to take effect
      Administration admin = new Administration(client, 240);
      admin.reload();

      client.close();
   }

   private static void backupDefaultConfiguration() {
      Path source = Paths.get(TestUtil.getJbossHome(), "standalone", "configuration", "standalone.xml");
      Path destination = Paths.get(TestUtil.getJbossHome(), "standalone", "configuration", "backup.xml");
      if (Files.exists(source)) {
            try {
               Files.copy(source, destination);
            } catch (IOException e) {
               LOG.error("Error:", e);
            }
      } else {
         LOG.error("Standalone.xml not found!");
      }

   }

   private static void resetConfiguration() throws Exception {
      Path source = Paths.get(TestUtil.getJbossHome(), "standalone", "configuration", "backup.xml");
      Path destination = Paths.get(TestUtil.getJbossHome(), "standalone", "configuration", "standalone.xml");
      if (Files.exists(source)) {
         try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(source);
         } catch (IOException e) {
            LOG.error("Error:", e);
         }
      } else {
         LOG.error("Backup.xml not found!");
      }

      // reload server
      OnlineManagementClient client = TestUtil.clientInit();
      Administration admin = new Administration(client, 240);
      admin.reload();
      client.close();
   }

   /**
    * Run bash/batch script that generates private/public keys, certificates, keystores and truststores for both client and server.
    */
   private static void generateCertificates() {
      File file = new File(RESOURCES);
      RESOURCES = file.getAbsolutePath();
      try {
         Process procBuildScript;
         if (TestUtil.isWindows()) {
            RESOURCES = RESOURCES.replace("\\","\\\\");
            procBuildScript = new ProcessBuilder(GENERATE_CERTIFICATES_SCRIPT_WINDOWS, HOSTNAME, RESOURCES).start();
         } else {
            procBuildScript = new ProcessBuilder(GENERATE_CERTIFICATES_SCRIPT_UNIX , HOSTNAME, RESOURCES).start();
         }
         procBuildScript.waitFor();
      } catch (IOException | InterruptedException e) {
         LOG.error("Certificate generation failed.", e);
      }
   }

}
