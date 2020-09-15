package org.jboss.resteasy.test.security;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
import org.jboss.resteasy.test.security.resource.BasicAuthRequestFilter;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseProxy;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResource;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResourceAnybody;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResourceMoreSecured;
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

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic test for RESTEasy authentication.
 * @tpSince RESTEasy 3.0.16
 */
@ServerSetup({BasicAuthTest.SecurityDomainSetup.class})
@RunWith(Arquillian.class)
@RunAsClient
@Category({
        ExpectedFailingOnWildFly18.class, //WFLY-12655
        NotForBootableJar.class // requires different security configuration
})
public class BasicAuthTest {

   private static final String WRONG_RESPONSE = "Wrong response content.";
   private static final String ACCESS_FORBIDDEN_MESSAGE = "Access forbidden: role not allowed";

   private static ResteasyClient authorizedClient;
   private static ResteasyClient unauthorizedClient;
   private static ResteasyClient noAutorizationClient;

    // Following clients are used in tests for ClientRequestFilter
    private static ResteasyClient authorizedClientUsingRequestFilter;
    private static ResteasyClient unauthorizedClientUsingRequestFilter;
    private static ResteasyClient unauthorizedClientUsingRequestFilterWithWrongPassword;

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
        // unauthorizedClient
        {
            UsernamePasswordCredentials credentials_other = new UsernamePasswordCredentials("ordinaryUser", "password2");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials_other);
            CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
           ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(client);
            unauthorizedClient = new ResteasyClientBuilder().httpEngine(engine).build();
        }
        // noAuthorizationClient
        noAutorizationClient = new ResteasyClientBuilder().build();

        // authorizedClient with ClientRequestFilter
        {
            authorizedClientUsingRequestFilter = new ResteasyClientBuilder().register(new BasicAuthRequestFilter("bill", "password1")).build();
        }
        // unauthorizedClient with ClientRequestFilter - unauthorized user
        {
            unauthorizedClientUsingRequestFilter = new ResteasyClientBuilder().register(new BasicAuthRequestFilter("ordinaryUser", "password2")).build();
        }
        // unauthorizedClient with ClientRequestFilter - wrong password
        {
            unauthorizedClientUsingRequestFilterWithWrongPassword = new ResteasyClientBuilder().register(new BasicAuthRequestFilter("bill", "password2")).build();
        }
    }

    @AfterClass
    public static void after() throws Exception {
        authorizedClient.close();
        unauthorizedClient.close();
        noAutorizationClient.close();
        authorizedClientUsingRequestFilter.close();
        unauthorizedClientUsingRequestFilter.close();
        unauthorizedClientUsingRequestFilterWithWrongPassword.close();
    }

   @Deployment
   public static Archive<?> deployLocatingResource() {
      WebArchive war = TestUtil.prepareArchive(BasicAuthTest.class.getSimpleName());

      Hashtable<String, String> contextParams = new Hashtable<String, String>();
      contextParams.put("resteasy.role.based.security", "true");

      war.addClass(BasicAuthBaseProxy.class)
            .addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web.xml", "/jboss-web.xml")
            .addAsWebInfResource(BasicAuthTest.class.getPackage(), "web.xml", "/web.xml");

      return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class,
            BasicAuthBaseResourceMoreSecured.class, BasicAuthBaseResourceAnybody.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, BasicAuthTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Basic ProxyFactory test. Correct credentials are used.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
   })
   public void testProxy() throws Exception {
      BasicAuthBaseProxy proxy = authorizedClient.target(generateURL("/")).proxyBuilder(BasicAuthBaseProxy.class).build();
      Assert.assertEquals(WRONG_RESPONSE, proxy.get(), "hello");
      Assert.assertEquals(WRONG_RESPONSE, proxy.getAuthorized(), "authorized");
   }

   /**
    * @tpTestDetails Basic ProxyFactory test. No credentials are used.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
   })
   public void testProxyFailure() throws Exception {
      BasicAuthBaseProxy proxy = noAutorizationClient.target(generateURL("/")).proxyBuilder(BasicAuthBaseProxy.class).build();
      try {
            proxy.getFailure();
            Assert.fail();
      } catch (NotAuthorizedException e) {
            Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, e.getResponse().getStatus());
            Assert.assertTrue("WWW-Authenticate header is not included", e.getResponse().getHeaderString("WWW-Authenticate").contains("Basic realm="));
      }
   }

   /**
    * @tpTestDetails Test secured resource with correct and incorrect credentials.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
   })
   public void testSecurity() throws Exception {
      // authorized client
      {
            Response response = authorizedClient.target(generateURL("/secured")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "hello", response.readEntity(String.class));
      }

      {
            Response response = authorizedClient.target(generateURL("/secured/authorized")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "authorized", response.readEntity(String.class));
      }

      {
            Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
      }
      {
            Response response = authorizedClient.target(generateURL("/secured3/authorized")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "authorized", response.readEntity(String.class));
      }

      // unauthorized client
      {
            Response response = unauthorizedClient.target(generateURL("/secured3/authorized")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
      }
      {
            Response response = unauthorizedClient.target(generateURL("/secured3/anybody")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
      }
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-579
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void test579() throws Exception {
      Response response = authorizedClient.target(generateURL("/secured2")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails Check failures for secured resource.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
   })
   public void testSecurityFailure() throws Exception {
      {
            Response response = noAutorizationClient.target(generateURL("/secured")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
            Assert.assertTrue("WWW-Authenticate header is not included", response.getHeaderString("WWW-Authenticate").contains("Basic realm="));
            response.close();
      }

      {
            Response response = authorizedClient.target(generateURL("/secured/authorized")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "authorized", response.readEntity(String.class));
      }

      {
            Response response = unauthorizedClient.target(generateURL("/secured/authorized")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            Assert.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
      }
   }

   /**
    * @tpTestDetails Regression test for JBEAP-1589, RESTEASY-1249
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAccesForbiddenMessage() throws Exception {
      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
      CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
      ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(client);

      ResteasyClient authorizedClient = new ResteasyClientBuilder().httpEngine(engine).build();
      Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
      Assert.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
      authorizedClient.close();
   }

   /**
    * @tpTestDetails Test Content-type when forbidden exception is raised, RESTEASY-1563
    * @tpSince RESTEasy 3.1.1
    */
   @Test
   @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
   })
   public void testContentTypeWithForbiddenMessage() {
      Response response = unauthorizedClient.target(generateURL("/secured/denyWithContentType")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
      Assert.assertEquals("Incorrect Content-type header", "text/html;charset=UTF-8", response.getHeaderString("Content-type"));
      Assert.assertEquals("Missing forbidden message in the response", ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Test Content-type when unauthorized exception is raised
    * @tpSince RESTEasy 3.1.1
    */
   @Test
   @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
   })
   public void testContentTypeWithUnauthorizedMessage() {
      Response response = noAutorizationClient.target(generateURL("/secured/denyWithContentType")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
      Assert.assertEquals("Incorrect Content-type header", "text/html;charset=UTF-8", response.getHeaderString("Content-type"));
      Assert.assertTrue("WWW-Authenticate header is not included", response.getHeaderString("WWW-Authenticate").contains("Basic realm="));
   }

    /**
     * @tpTestDetails Test secured resource with correct credentials. Authentication is done using BasicAuthRequestFilter.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
    })
    public void testWithClientRequestFilterAuthorizedUser() {
        Response response = authorizedClientUsingRequestFilter.target(generateURL("/secured/authorized")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE, "authorized", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test secured resource with incorrect credentials. Authentication is done using BasicAuthRequestFilter.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
    })
    public void testWithClientRequestFilterWrongPassword(){
        Response response = unauthorizedClientUsingRequestFilterWithWrongPassword.target(generateURL("/secured/authorized")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        Assert.assertTrue("WWW-Authenticate header is not included", response.getHeaderString("WWW-Authenticate").contains("Basic realm="));
    }

    /**
     * @tpTestDetails Test secured resource with correct credentials of user that is not authorized to the resource. Authentication is done using BasicAuthRequestFilter.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    @Category({
        // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
        ExpectedFailingWithStandaloneMicroprofileConfiguration.class
    })
    public void testWithClientRequestFilterUnauthorizedUser() {
        Response response = unauthorizedClientUsingRequestFilter.target(generateURL("/secured/authorized")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE, ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test that client correctly loads ClientConfigProvider implementation and uses credentials when making a request.
     * Also test these credentials are ignored if different are set.
     */
    @Test
    @Category({
            // MP is missing security, setup fails: "operation" => "add","address" => [("subsystem" => "security"),("security-domain" => "jaxrsSecDomain")]} failed
            ExpectedFailingWithStandaloneMicroprofileConfiguration.class
    })
    public void testClientConfigProviderCredentials() throws IOException, InterruptedException {
        String jarPath = ClientConfigProviderTestJarHelper.createClientConfigProviderTestJarWithBASIC();

        Process process = ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_CREDENTIALS_ARE_USED_FOR_BASIC,
                jarPath,
                new String[]{generateURL("/secured/authorized")});
        String line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assert.assertEquals("200", line);
        process.destroy();

        process = ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_CLIENTCONFIG_CREDENTIALS_ARE_IGNORED_IF_DIFFERENT_SET,
                jarPath,
                new String[]{generateURL("/secured/authorized")});
        line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assert.assertEquals("401", line);
        process.destroy();

        Assert.assertTrue(new File(jarPath).delete());
    }

    static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {

      @Override
      public void setConfigurationPath() throws URISyntaxException {
            Path filepath= Paths.get(BasicAuthTest.class.getResource("users.properties").toURI());
            Path parent = filepath.getParent();
            createPropertiesFiles(new File(parent.toUri()));
      }

   }
}
