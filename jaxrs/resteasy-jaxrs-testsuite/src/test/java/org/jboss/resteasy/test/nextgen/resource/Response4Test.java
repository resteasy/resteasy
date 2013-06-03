package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

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
