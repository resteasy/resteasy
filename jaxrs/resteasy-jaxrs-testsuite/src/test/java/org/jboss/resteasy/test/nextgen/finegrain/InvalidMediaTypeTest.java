package org.jboss.resteasy.test.nextgen.finegrain;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-699.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date August 20, 2013
 */
public class InvalidMediaTypeTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("test")
   static public class TestResource
   {
      @GET
      @Produces("*/*")
      public Response test()
      {
         return Response.ok().entity("ok").build();
      }
   }

   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testInvalidMediaTypes() throws Exception
   {
      before();
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target(TestPortProvider.generateURL("/test")).request();
      
      // Missing type or subtype
      doTest(request, "/");
      doTest(request, "/*");
      doTest(request, "*/");
      doTest(request, "text/");
      doTest(request, "/plain");
      
      // Illegal white space
      doTest(request, " /*");
      doTest(request, "/* ");
      doTest(request, " /* ");
      doTest(request, "/ *");
      doTest(request, "* /");
      doTest(request, " / *");
      doTest(request, "* / ");
      doTest(request, "* / *");
      doTest(request, " * / *");
      doTest(request, "* / * ");
      doTest(request, "text/ plain");
      doTest(request, "text /plain");
      doTest(request, " text/plain");
      doTest(request, "text/plain ");
      doTest(request, " text/plain ");
      doTest(request, " text / plain ");
      
      after();
   }
   
   private void doTest(Invocation.Builder request, String mediaType)
   {
      request.accept(mediaType);
      Response response = request.get();
      System.out.println("\rmediaType: \"" + mediaType + "\"");
      System.out.println("\rstatus: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      response.close();
   }
}
