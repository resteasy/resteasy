package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientErrorBadMediaTypeTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class WebResourceUnsupportedMediaType
   {
      @Consumes("application/bar")
      @Produces("application/foo")
      @POST
      public String doPost(String entity)
      {
         return "content";
      }

      @Produces("text/plain")
      @GET
      @Path("complex/match")
      public String get()
      {
         return "content";
      }

      @Produces("text/xml")
      @GET
      @Path("complex/{uriparam: [^/]+}")
      public String getXml(@PathParam("uriparam") String param)
      {
         return "<" + param + "/>";
      }

      @DELETE
      public void delete()
      {

      }

      @Path("/nocontent")
      @POST
      public void noreturn(String entity)
      {

      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
//      ResteasyProviderFactory factory = new ResteasyProviderFactory();
//      factory.addHeaderDelegate(MediaType.class, new TestMediaTypeHeaderDelegate());
//      ResteasyDeployment deployment = new ResteasyDeployment();
//      deployment.setProviderFactory(factory);
//      EmbeddedContainer.start(deployment);
//      dispatcher = deployment.getDispatcher();
//      dispatcher.getRegistry().addPerRequestResource(WebResourceUnsupportedMediaType.class);
      

      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(WebResourceUnsupportedMediaType.class);

    }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }


   /**
    * This test uses an extension of MediaTypeHeaderDelegate because
    * ClientRequest.body() uses MediaType to parse media types, which, 
    * in turn, calls the ResteasyProviderFactory to get an instance of
    * MediaTypeHeaderDelegate.  But MediaTypeHeaderDelegate will not
    * accept the ill-formed media type "text", so the ClientRequest
    * cannot even be executed.
    */
   @Test
   public void testBadContentType() throws Exception
   {

      // Configure use of TestMediaTypeHeaderDelegate.
//      ResteasyProviderFactory factory = new ResteasyProviderFactory();
//      factory.addHeaderDelegate(MediaType.class, new TestMediaTypeHeaderDelegate());
//      ResteasyDeployment deployment = new ResteasyDeployment();
//      deployment.setProviderFactory(factory);
//      EmbeddedContainer.start(deployment);
//      
//      Field delegate = MediaType.class.getDeclaredField("delegate");
//      delegate.setAccessible(true);
//      Field modifiers = Field.class.getDeclaredField("modifiers");
//      modifiers.setAccessible(true);
//      modifiers.setInt(delegate, delegate.getModifiers() & ~Modifier.FINAL);
//      delegate.set(null, new TestMediaTypeHeaderDelegate());
      
//      deployment.getRegistry().addPerRequestResource(WebResourceUnsupportedMediaType.class);
//      System.out.println("HeaderDelegate<MediaType>: " + MediaType.getDelegate());
      
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.body("foo/bar", "content");
      try
      {
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(HttpResponseCodes.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
   
   static class TestMediaTypeHeaderDelegate extends MediaTypeHeaderDelegate
   {
      public Object fromString(String type) throws IllegalArgumentException
      {
         if (type == null) throw new IllegalArgumentException("MediaType value is null");
         return parse(type);
      }
      
      public static MediaType parse(String type)
      {
         if ("text".equals(type))
         {
            return new MediaType("text", "");
         }
         return MediaTypeHeaderDelegate.parse(type);
      }
   }
}
