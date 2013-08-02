package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SetRequestUriTest
{

   @Path("resource")
   public static class Resource {

      @GET
      @Path("setrequesturi1uri")
      public String setRequestUri() {
         return "OK";
      }

      @GET
      @Path("setrequesturi1")
      public String setRequestUriDidNotChangeUri() {
         return "Filter did not change the uri to go to";
      }
   }

   @Provider
   @Priority(100)
   @PreMatching
   public static class RequestFilter implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         URI uri = null;
         try {
            UriInfo info = requestContext.getUriInfo();
            String path = new StringBuilder().append(info.getAbsolutePath())
                    .append("uri").toString();
            uri = new java.net.URI(path);
         } catch (URISyntaxException e) {
            throw new IOException(e);
         }
         requestContext.setRequestUri(uri);

      }
   }

   static Client client;
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @BeforeClass
   public static void setup() throws Exception
   {
      deployment = EmbeddedContainer.start("/base");
      dispatcher = deployment.getDispatcher();
      client = ClientBuilder.newClient();
      deployment.getProviderFactory().register(RequestFilter.class);
      deployment.getRegistry().addPerRequestResource(Resource.class);
   }


   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
      client.close();
   }

   @Test
   public void testResolve()
   {
      URI base = URI.create("http://localhost:888/otherbase");
      URI uri = URI.create("http://xx.yy:888/base/resource/sub");

      System.out.println(base.resolve(uri));

   }



   @Test
   public void testUriOverride()
   {
      Response response = client.target(generateURL("/base/resource/setrequesturi1")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("OK", response.readEntity(String.class));

   }

}
