package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * Test POJO constructor/field injection.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConstructedInjectionTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
   }

   @AfterClass
   public static void after() throws Exception
   {
   }

   @Path("/")
   public static class ConstructedResource
   {
      UriInfo myInfo;

      String abs;

      public ConstructedResource(@Context UriInfo myInfo, @QueryParam("abs") String abs)
      {
         this.myInfo = myInfo;
         this.abs = abs;
      }

      @Path("/simple")
      @GET
      public String get()
      {
         System.out.println("abs query: " + abs);
         URI base = null;
         if (abs == null)
         {
            base = createURI("/");
         }
         else
         {
            base = createURI("/" + abs + "/");
         }

         System.out.println("BASE URI: " + myInfo.getBaseUri());
         System.out.println("Request URI: " + myInfo.getRequestUri());
         Assert.assertEquals(base.getPath(), myInfo.getBaseUri().getPath());
         Assert.assertEquals("/simple", myInfo.getPath());
         return "CONTENT";
      }

   }

   private void _test(String path)
   {
      ClientRequest request = new ClientRequest(generateURL(path));
      try
      {
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testUriInfo() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(ConstructedResource.class);
         _test("/simple");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

}