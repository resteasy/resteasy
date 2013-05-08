package org.jboss.resteasy.test.client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseFilterTest
{
   public static class AbortWith implements ClientRequestFilter
   {
      private Response abortWith;

      public AbortWith(Response abortWith)
      {
         this.abortWith = abortWith;
      }

      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         requestContext.abortWith(abortWith);
      }
   }

   public static class StatusOverrideFilter implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         responseContext.setStatus(Response.Status.FORBIDDEN.getStatusCode());
      }
   }

   public static class AllowedFilter implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         Set<String> allowed = responseContext.getAllowedMethods();
         Assert.assertTrue(allowed.contains("OPTIONS"));
      }
   }

   public static class LengthFilter implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         Assert.assertEquals(10, responseContext.getLength());
      }
   }


   static Client client;

   @BeforeClass
   public static void setupClient()
   {
      client = ClientBuilder.newClient();

   }

   @AfterClass
   public static void close()
   {
      client.close();
   }

   public static class NullHeaderStringFilter implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         String header = responseContext.getHeaderString("header1");
         Assert.assertTrue(header != null);
         Assert.assertTrue(header.equals(""));
      }
   }

   @Test
   public void testEmptyHeaderString()
   {

      RuntimeDelegate original = RuntimeDelegate.getInstance();
      RuntimeDelegate
              .setInstance(new NullStringBeanRuntimeDelegate(original));
      try {
         Response abortWith = Response.ok().header("header1", new StringBean("aa"))
                 .build();
         Response response = client.target("dummy").register(new AbortWith(abortWith)).register(NullHeaderStringFilter.class).request().get();
         Assert.assertEquals(response.getStatus(), 200);
      } finally {
         RuntimeDelegate.setInstance(original);
         StringBeanRuntimeDelegate.assertNotStringBeanRuntimeDelegate();
      }

   }

   @Test
   public void testLength()
   {
      Response abortWith = Response.ok()
              .header(HttpHeaders.CONTENT_LENGTH, 10).build();
      Response response = client.target("dummy").register(new AbortWith(abortWith)).register(LengthFilter.class).request().get();
      Assert.assertEquals(response.getStatus(), 200);

   }

   @Test
   public void testAllowed()
   {
      Response abortWith = Response.ok().header(HttpHeaders.ALLOW, "get")
              .header(HttpHeaders.ALLOW, "options").build();
      Response response = client.target("dummy").register(new AbortWith(abortWith)).register(AllowedFilter.class).request().get();
      Assert.assertEquals(response.getStatus(), 200);

   }

   @Test
   public void testStatusOverride()
   {
      Response response = client.target("dummy").register(new AbortWith(Response.ok().build())).register(StatusOverrideFilter.class).request().get();
      Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());

   }

}
