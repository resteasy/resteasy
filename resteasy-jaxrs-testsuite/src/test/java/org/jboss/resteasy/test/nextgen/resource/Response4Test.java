package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Response4Test extends BaseResourceTest
{
   @Path("/")
   @Produces("text/plain")
   public static class Resource
   {

      @POST
      @Path("empty")
      public void empty()
      {
      }

      @GET
      @Path("default_head")
      public Response defaultHead()
      {
         return Response.ok(" ").build();
      }
   }



   @Provider
   public static class ResponseFilter implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         responseContext.setStatus(201);
      }
   }


   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(ResponseFilter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      client.close();
   }

   @Test
   public void testDefaultHead()
   {
      // mucks up stream so create our own client.
      //Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/default_head")).request().head();
      Assert.assertEquals(201, response.getStatus());
      Assert.assertNotNull(response.getMediaType());
      System.out.println(response.getMediaType());
      Assert.assertTrue(response.getMediaType().equals(MediaType.TEXT_PLAIN_TYPE));
      response.close();
      //client.close();

   }


   @Test
   public void testChangeStatus()
   {
      Response response = client.target(generateURL("/empty")).request().post(null);
      Assert.assertEquals(201, response.getStatus());
      response.close();

   }
}
