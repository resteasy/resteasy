package org.jboss.resteasy.test.security;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly18;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.setup.AbstractUsersRolesSecurityDomainSetup;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Two different security domains in two deployments. Both domains are by default created in PicketBox
 * security subsystem. When running server and tests Elytron enabled, domain in the deployment 2 is created
 * in the Elytron subsystem.
 * @tpSince RESTEasy 3.0.21
 */
@ServerSetup({TwoSecurityDomainsTest.SecurityDomainSetup1.class, TwoSecurityDomainsTest.SecurityDomainSetup2.class})
@RunWith(Arquillian.class)
@RunAsClient
@Category({
    ExpectedFailingOnWildFly18.class,               //WFLY-12655
    ExpectedFailingWithStandaloneMicroprofileConfiguration.class,
    NotForBootableJar.class // requires different security configuration
})
public class TwoSecurityDomainsTest {

   private static ResteasyClient authorizedClient;
   private static final String SECURITY_DOMAIN_DEPLOYMENT_1 = "jaxrsSecDomain";
   private static final String SECURITY_DOMAIN_DEPLOYMENT_2 = "jaxrsSecDomain2";
   private static final String WRONG_RESPONSE = "Wrong response content.";

   @Deployment(name= "SECURITY_DOMAIN_DEPLOYMENT_1")
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_1);

      Hashtable<String, String> contextParams = new Hashtable<String, String>();
      contextParams.put("resteasy.role.based.security", "true");

      war.addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web.xml", "/jboss-web.xml")
            .addAsWebInfResource(TwoSecurityDomainsTest.class.getPackage(), "web.xml", "/web.xml");

      return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class);
   }

   @Deployment(name= "SECURITY_DOMAIN_DEPLOYMENT_2")
   public static Archive<?> deploy2() {
      WebArchive war = TestUtil.prepareArchive(TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_2);

      Hashtable<String, String> contextParams = new Hashtable<String, String>();
      contextParams.put("resteasy.role.based.security", "true");

      war.addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web2.xml", "/jboss-web.xml")
            .addAsWebInfResource(TwoSecurityDomainsTest.class.getPackage(), "web.xml", "/web.xml");

      return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class);
   }

   @BeforeClass
   public static void init() {
      // authorizedClient
      {
         UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
         CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
         credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
         CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
         ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(client);
         authorizedClient = new ResteasyClientBuilder().httpEngine(engine).build();
      }
   }

   @AfterClass
   public static void after() throws Exception {
      authorizedClient.close();
   }

   /**
    * @tpTestDetails Client using correct authorization credentials sends GET request to the first and then second deployment
    * @tpSince RESTEasy 3.0.21
    */
   @Test
   public void testOneClientTwoDeploymentsTwoSecurityDomains() throws Exception {
      Response response = authorizedClient.target(PortProviderUtil.generateURL("/secured", TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_1)).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(WRONG_RESPONSE, "hello", response.readEntity(String.class));

      response = authorizedClient.target(PortProviderUtil.generateURL("/secured", TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_2)).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(WRONG_RESPONSE, "hello", response.readEntity(String.class));
   }

   static class SecurityDomainSetup1 extends AbstractUsersRolesSecurityDomainSetup {

      @Override
      public void setConfigurationPath() throws URISyntaxException {
         Path filepath= Paths.get(TwoSecurityDomainsTest.class.getResource("users.properties").toURI());
         Path parent = filepath.getParent();
         createPropertiesFiles(new File(parent.toUri()));
         setSecurityDomainName(SECURITY_DOMAIN_DEPLOYMENT_1);
         setSubsystem("picketBox");
      }
   }

   static class SecurityDomainSetup2 extends AbstractUsersRolesSecurityDomainSetup {

      @Override
      public void setConfigurationPath() throws URISyntaxException {
         Path filepath= Paths.get(TwoSecurityDomainsTest.class.getResource("users.properties").toURI());
         Path parent = filepath.getParent();
         createPropertiesFiles(new File(parent.toUri()));
         setSecurityDomainName(SECURITY_DOMAIN_DEPLOYMENT_2);
      }
   }
}
