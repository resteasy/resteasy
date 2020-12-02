package org.jboss.resteasy.embedded.test.core.validate;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @tpSubChapter Verify ValidatorFactory is found in absence of CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2549
 * @tpSince RESTEasy 3.14.0
 */
public class NonCDIValidatorFactoryTest
{
   private static UndertowJaxrsServer server;

   public static class TestApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @Path("test")
   public static class TestResource {

      @GET
      @Path("validate/{n}")
      @Produces("text/plain")
      public Response string(@Size(min=3) String s) {
         return Response.ok("ok").build();
      }
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void before() throws Exception
   {
      server = new UndertowJaxrsServer().start();
      server.deploy(TestApp.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      server.stop();
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testValidate() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Invocation.Builder request = client.target("http://localhost:8081/test/validate/x").request();
      Response response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull("Validation header is missing", header);
      Assert.assertTrue("Wrong validation header", Boolean.valueOf(header));
      String entity = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(entity);
      TestUtil.countViolations(r, 0, 0, 1, 0);
      client.close();
   }
}
