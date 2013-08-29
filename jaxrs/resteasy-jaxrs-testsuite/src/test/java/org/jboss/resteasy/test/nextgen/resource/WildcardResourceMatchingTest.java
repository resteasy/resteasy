package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WildcardResourceMatchingTest extends BaseResourceTest
{
   @Path("main")
   public static class MainResource {
      @GET
      public String subresource() {
         return this.getClass().getSimpleName();
      }
   }

   @Path("main/{key}")
   public static class MainSubResource {
      @GET
      public String subresource() {
         return this.getClass().getSimpleName();
      }
   }

   @Path("main/{key}/{subkey}")
   public static class MainSubSubResource {
      @GET
      public String subresource() {
         return this.getClass().getSimpleName();
      }
   }


   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(MainResource.class);
      addPerRequestResource(MainSubResource.class);
      addPerRequestResource(MainSubSubResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testMain()
   {
      Response response = client.target(generateURL("/main")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("MainResource", response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testMainSub()
   {
      Response response = client.target(generateURL("/main/sub")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("MainSubResource", response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testMainSubSub()
   {
      Response response = client.target(generateURL("/main/sub/sub")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("MainSubSubResource", response.readEntity(String.class));
      response.close();
   }

}
