package org.jboss.resteasy.test.finegrain;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
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
      
      // Missing type or subtype
      doTest("/");
      doTest("/*");
      doTest("*/");
      doTest("text/");
      doTest("/plain");
      
      // Illegal white space
      doTest(" /*");
      doTest("/* ");
      doTest(" /* ");
      doTest("/ *");
      doTest("* /");
      doTest(" / *");
      doTest("* / ");
      doTest("* / *");
      doTest(" * / *");
      doTest("* / * ");
      doTest("text/ plain");
      doTest("text /plain");
//      doTest(" text/plain");  " text/plain" gets turned into "text/plain"
//      doTest("text/plain ");  "text/plain " gets turned into "text/plain"
//      doTest(" text/plain "); " text/plain " gets turned into "text/plain"
      doTest(" text / plain ");
      
      after();
   }
   
   private void doTest(String mediaType) throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.accept(mediaType);
      Response response = request.get();
      System.out.println("\rmediaType: \"" + mediaType + "\"");
      System.out.println("\rstatus: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
   }
}
