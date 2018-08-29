package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.logging.Logger;
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

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EncodedPathTest
{
   private static Dispatcher dispatcher;
   private static final Logger LOG = Logger.getLogger(EncodedPathTest.class);

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
      @Path("/hello world")
      @GET
      public String get()
      {
         LOG.info("Hello");
         return "HELLO";
      }

      @Path("/goodbye%7Bworld")
      @GET
      public String goodbye()
      {
         LOG.info("Goodbye");
         return "GOODBYE";
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
   public void testEncoded() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
         _test("/hello%20world");
         _test("/goodbye%7Bworld");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
}