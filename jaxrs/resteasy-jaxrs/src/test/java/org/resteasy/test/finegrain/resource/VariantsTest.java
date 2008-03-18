package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VariantsTest
{
   private static HttpServletDispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      dispatcher.getRegistry().addResource(LanguageVariantResource.class);
      dispatcher.getRegistry().addResource(ComplexVariantResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   @Path("/")
   public static class LanguageVariantResource
   {
      @GET
      public Response doGet(@Context Request r)
      {
         List<Variant> vs = Variant.VariantListBuilder.newInstance().
                 languages("zh").
                 languages("fr").
                 languages("en").add().
                 build();

         Variant v = r.selectVariant(vs);
         if (v == null)
            return Response.notAcceptable(vs).build();
         else
            return Response.ok(v.getLanguage(), v).build();
      }
   }

   @Test
   public void testGetLanguageEn() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("en", method.getResponseBodyAsString());
         Assert.assertEquals("en", method.getResponseHeader(HttpHeaderNames.CONTENT_LANGUAGE).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testGetLanguageZh() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "zh");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("zh", method.getResponseBodyAsString());
         Assert.assertEquals("zh", method.getResponseHeader(HttpHeaderNames.CONTENT_LANGUAGE).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testGetLanguageMultiple() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en;q=0.3, zh;q=0.4, fr");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("fr", method.getResponseBodyAsString());
         Assert.assertEquals("fr", method.getResponseHeader(HttpHeaderNames.CONTENT_LANGUAGE).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Path("/complex")
   public static class ComplexVariantResource
   {
      @GET
      public Response doGet(@Context Request r)
      {
         List<Variant> vs = Variant.VariantListBuilder.newInstance().
                 mediaTypes(MediaType.parse("image/jpeg")).add().
                 mediaTypes(MediaType.parse("application/xml")).languages("en-us").add().
                 mediaTypes(MediaType.parse("text/xml")).languages("en").add().
                 mediaTypes(MediaType.parse("text/xml")).languages("en-us").add().
                 build();

         Variant v = r.selectVariant(vs);
         if (v == null)
            return Response.notAcceptable(vs).build();
         else
            return Response.ok("GET", v).build();
      }
   }

   @Test
   public void testGetComplex1() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/complex");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "image/png");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en;q=0.5");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("GET", method.getResponseBodyAsString());
         Assert.assertEquals("text/xml", method.getResponseHeader(HttpHeaderNames.CONTENT_TYPE).getValue());
         Assert.assertEquals("en-us", method.getResponseHeader(HttpHeaderNames.CONTENT_LANGUAGE).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testGetComplex2() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/complex");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "image/png");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en, en-us");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("GET", method.getResponseBodyAsString());
         Assert.assertEquals("text/xml", method.getResponseHeader(HttpHeaderNames.CONTENT_TYPE).getValue());
         Assert.assertEquals("en", method.getResponseHeader(HttpHeaderNames.CONTENT_LANGUAGE).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testGetComplex3() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/complex");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "image/png");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en;q=0.5");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("GET", method.getResponseBodyAsString());
         Assert.assertEquals("application/xml", method.getResponseHeader(HttpHeaderNames.CONTENT_TYPE).getValue());
         Assert.assertEquals("en-us", method.getResponseHeader(HttpHeaderNames.CONTENT_LANGUAGE).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testGetComplexNotAcceptable() throws IOException
   {
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/complex");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/atom+xml");
         method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, 406);
            String vary = method.getResponseHeader(HttpHeaderNames.VARY).getValue();
            Assert.assertNotNull(vary);
            System.out.println("vary: " + vary);
            Assert.assertTrue(contains(vary, "Accept"));
            Assert.assertTrue(contains(vary, "Accept-Language"));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/complex");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xml");
         method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "fr");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, 406);
            String vary = method.getResponseHeader(HttpHeaderNames.VARY).getValue();
            Assert.assertNotNull(vary);
            Assert.assertTrue(contains(vary, "Accept"));
            Assert.assertTrue(contains(vary, "Accept-Language"));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   private boolean contains(String l, String v)
   {
      String[] vs = l.split(",");
      for (String s : vs)
      {
         s = s.trim();
         if (s.equalsIgnoreCase(v))
            return true;
      }

      return false;
   }
}
