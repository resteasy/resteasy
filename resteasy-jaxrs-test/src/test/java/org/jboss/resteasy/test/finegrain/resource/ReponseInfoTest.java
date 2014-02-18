package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReponseInfoTest
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
   public static class SimpleResource
   {
      @Path("/simple")
      @GET
      public String get(@QueryParam("abs") String abs)
      {
         System.out.println("abs query: " + abs);
         URI base = null;
         if (abs == null)
         {
            base = createURI("/new/one");
         }
         else
         {
            base = createURI("/" + abs + "/new/one");
         }
         Response response = Response.temporaryRedirect(URI.create("new/one")).build();
         URI uri = (URI) response.getMetadata().getFirst(HttpHeaderNames.LOCATION);
         System.out.println("Location uri: " + uri);
         Assert.assertEquals(base.getPath(), uri.getPath());
         return "CONTENT";
      }
   }

   private void _test(String path)
   {
      ClientRequest request = new ClientRequest(generateURL(path));
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
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
         dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
         _test("/simple");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   @Test
   public void testUriInfo2() throws Exception
   {
      dispatcher = EmbeddedContainer.start("/resteasy").getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
         _test("/resteasy/simple?abs=resteasy");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

}