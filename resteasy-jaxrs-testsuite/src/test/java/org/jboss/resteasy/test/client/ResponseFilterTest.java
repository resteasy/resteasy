package org.jboss.resteasy.test.client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
   public void getStringHeadersUsingHeaderDelegateTest()
   {
      RuntimeDelegate original = RuntimeDelegate.getInstance();
      RuntimeDelegate.setInstance(new StringBeanRuntimeDelegate(original));
      try {
         StringBuilder builder = new StringBuilder("s1");
         StringBuffer buffer = new StringBuffer("s2");
         StringBean bean = new StringBean("s3");
         Response response = Response.ok()
                 .header(builder.toString(), builder)
                 .header(buffer.toString(), buffer).header(bean.get(), bean)
                 .build();
         MultivaluedMap<String, String> headers = response
                 .getStringHeaders();
         String header = headers.getFirst(bean.get());
         Assert.assertTrue(bean.get().equalsIgnoreCase(header));
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

   protected static ClientRequestFilter createRequestFilter(
           final Response response) {
      ClientRequestFilter outFilter = new ClientRequestFilter() {

         @Override
         public void filter(ClientRequestContext context) throws IOException {
            Response r;
            if (response == null)
               r = Response.ok().build();
            else
               r = response;
            context.abortWith(r);
         }
      };
      return outFilter;
   }

   public static class HeadersFilter implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         for (Map.Entry<String, List<String>> header : responseContext.getHeaders().entrySet())
         {
            System.out.print(header.getKey() + ": ");
            for (String val : header.getValue()) System.out.print(", " + val);
         }
      }
   }


   @Test
   public void testHeaders()
   {
      // test that response headers are all strings
      // don't set entity content type to test that we set it correctly too
      Response.ResponseBuilder builder = Response.ok()
           .header("header", MediaType.APPLICATION_ATOM_XML_TYPE)
           .entity("entity");
      Response fake = builder.build();

      Client client = ClientBuilder.newClient();
      client.register(createRequestFilter(fake));
      Response response = client.target("http://nowhere").register(createRequestFilter(fake))
              .register(HeadersFilter.class).request().get();
   }

   @Priority(100)
   public static class InterceptorReaderOne implements ReaderInterceptor
   {
      @Override
      public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
      {
         try {
            return context.proceed();
         }
         catch (IOException e) {
            return "OK";
         }
      }
   }

   @Priority(200)
   public static class InterceptorReaderTwo implements ReaderInterceptor
   {
      @Override
      public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
      {
         throw new IOException("should be caught");
      }
   }

   @Test
   public void testInterceptorOrder()
   {
      Response.ResponseBuilder builder = Response.ok()
              .header("header", MediaType.APPLICATION_ATOM_XML_TYPE)
              .entity("entity");
      Response fake = builder.build();

      Client client = ClientBuilder.newClient();
      client.register(createRequestFilter(fake));
      Response response = client.target("http://nowhere").register(createRequestFilter(fake))
              .register(InterceptorReaderTwo.class)
              .register(InterceptorReaderOne.class)
              .request().get();
      String str = response.readEntity(String.class);
   }




}
