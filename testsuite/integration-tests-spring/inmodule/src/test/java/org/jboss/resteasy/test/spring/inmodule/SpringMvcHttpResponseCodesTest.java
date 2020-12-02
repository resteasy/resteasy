package org.jboss.resteasy.test.spring.inmodule;

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
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.setup.AbstractUsersRolesSecurityDomainSetup;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.spring.inmodule.resource.SpringMvcHttpResponseCodesPerson;
import org.jboss.resteasy.test.spring.inmodule.resource.SpringMvcHttpResponseCodesResource;
import org.jboss.resteasy.test.spring.inmodule.resource.TestResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests various http response codes returned from the server
 * @tpSince RESTEasy 3.1.0
 */
@ServerSetup({SpringMvcHttpResponseCodesTest.SecurityDomainSetup.class})
@RunWith(Arquillian.class)
@RunAsClient
public class SpringMvcHttpResponseCodesTest {
   private static Client authorizedClient;
   private static Client userAuthorizedClient;
   private static Client nonAutorizedClient;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(SpringMvcHttpResponseCodesTest.class.getSimpleName());
      war.addAsWebInfResource(SpringMvcHttpResponseCodesTest.class.getPackage(), "springMvcHttpResponseCodes/web-secure.xml", "web.xml");
      war.addAsWebInfResource(SpringMvcHttpResponseCodesTest.class.getPackage(), "springMvcHttpResponseCodes/jboss-web.xml", "jboss-web.xml");
      war.addAsWebInfResource(SpringMvcHttpResponseCodesTest.class.getPackage(), "springMvcHttpResponseCodes/mvc-dispatcher-servlet.xml", "mvc-dispatcher-servlet.xml");
      war.addAsWebInfResource(SpringMvcHttpResponseCodesTest.class.getPackage(), "springMvcHttpResponseCodes/applicationContext.xml", "applicationContext.xml");
      war.addAsManifestResource(new StringAsset("Dependencies: org.springframework.spring meta-inf\n"), "MANIFEST.MF");
      war.addClass(SpringMvcHttpResponseCodesPerson.class);
      return TestUtil.finishContainerPrepare(war, null, SpringMvcHttpResponseCodesResource.class, TestResource.class);
   }

   @Before
   public void init() {
      // authorized client
      {
         UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
         CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
         credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
         CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
         ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(client);
         authorizedClient = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).httpEngine(engine).build();
      }

      // userAuthorizedClient
      {
         UsernamePasswordCredentials credentials_other = new UsernamePasswordCredentials("ordinaryUser", "password2");
         CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
         credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials_other);
         CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
         ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(client);
         userAuthorizedClient = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).httpEngine(engine).build();
      }

      // non-authorized client
      nonAutorizedClient = ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      authorizedClient.close();
      userAuthorizedClient.close();
      nonAutorizedClient.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, SpringMvcHttpResponseCodesTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test server http response code for NotAcceptableException
    * (The resource produces text/plain responses, while the client accepts application/json only)
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testNotAcceptableException() {
      Response response = authorizedClient.target(generateURL("/" + TestResource.TEST_PATH)).request()
            .accept(MediaType.APPLICATION_JSON_TYPE).get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
   }

   /**
    * @tpTestDetails Test server http response code for NotFoundException
    * (The client sends a GET to a URL that does not exist)
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testNotFoundException() {
      Response response = authorizedClient.target(generateURL("/dummy")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
   }

   /**
    * @tpTestDetails Test server http response code for NotFoundException
    * (The client sends a POST request to a resource path accepting GET only)
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testMethodNotAllowedException() {
      Response response = authorizedClient.target(generateURL("/" + TestResource.TEST_PATH)).request().post(null);
      Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
   }

   /**
    * @tpTestDetails Test server http response code for BadRequestException
    * (The client sends a bad request, not matching expected data format)
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testBadRequestException() {
      Response response = authorizedClient.target(generateURL("/" + TestResource.TEST_PATH + "/json")).request()
            .post(Entity.entity("[{customer:\"Zack\"}]", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
   }

   /**
    * @tpTestDetails Test server http response code for NotSupportedException
    * (The client posts an application/xml request, while the server only accepts application/json
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testNotSupportedException() {
      Response response = authorizedClient.target(generateURL("/" + TestResource.TEST_PATH + "/json")).request()
            .post(Entity.entity("[{name:\"Zack\"}]", MediaType.APPLICATION_XML_TYPE));
      Assert.assertEquals(HttpResponseCodes.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
   }

   /**
    * @tpTestDetails Test server http response code for NotAuthorizedException using client without credentials
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testNotAuthorizedException() {
      Response response = nonAutorizedClient.target(generateURL("/secured/json")).request()
            .post(Entity.entity("{\"name\":\"Zack\"}", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
   }

   /**
    * @tpTestDetails Test server http response code for ForbiddenException using client with credentials which are not
    * sufficient to grant access to the resource
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testForbiddenException() {
      Response response = userAuthorizedClient.target(generateURL("/secured/json")).request()
            .post(Entity.entity("{\"name\":\"Zack\"}", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
   }

   @Test
   public void testOK() {
      Response response = authorizedClient.target(generateURL("/secured/json")).request()
            .post(Entity.entity("{\"name\":\"Zack\"}", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
   }

   static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {
      SecurityDomainSetup() {
         super(SpringMvcHttpResponseCodesTest.class.getResource("users.properties"),
                 SpringMvcHttpResponseCodesTest.class.getResource("roles.properties"));
      }
   }
}
