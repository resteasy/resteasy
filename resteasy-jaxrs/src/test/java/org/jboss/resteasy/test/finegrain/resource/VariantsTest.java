package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VariantsTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(LanguageVariantResource.class);
      dispatcher.getRegistry().addPerRequestResource(ComplexVariantResource.class);
      dispatcher.getRegistry().addPerRequestResource(EncodingVariantResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class LanguageVariantResource
   {
      @GET
      public Response doGet(@Context Request r)
      {
         List<Variant> vs = Variant.VariantListBuilder.newInstance().languages(new Locale("zh")).languages(
               new Locale("fr")).languages(new Locale("en")).add().build();

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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
         List<Variant> vs = Variant.VariantListBuilder.newInstance().mediaTypes(MediaType.valueOf("image/jpeg")).add()
               .mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().mediaTypes(
                     MediaType.valueOf("text/xml")).languages(new Locale("en")).add().mediaTypes(
                     MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().build();

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
      GetMethod method = createGetMethod("/complex");
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
      GetMethod method = createGetMethod("/complex");
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
      GetMethod method = createGetMethod("/complex");
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
         GetMethod method = createGetMethod("/complex");
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
         GetMethod method = createGetMethod("/complex");
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

   @Path("/encoding")
   public static class EncodingVariantResource
   {
      @GET
      public Response doGet(@Context Request r)
      {
         List<Variant> vs = Variant.VariantListBuilder.newInstance().languages(new Locale("en")).encodings("enc1", "enc2", "enc3").add().build();
         Variant v = r.selectVariant(vs);
         if (v == null)
            return Response.notAcceptable(vs).build();
         else
            return Response.ok(v.getEncoding(), v).build();
      }
   }

   @Test
   public void testGetEncoding1() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/encoding");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_ENCODING, "enc1");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("enc1", method.getResponseBodyAsString());
         Assert.assertEquals("enc1", method.getResponseHeader(HttpHeaderNames.CONTENT_ENCODING).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testGetEncoding2() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/encoding");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_ENCODING, "enc2");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("enc2", method.getResponseBodyAsString());
         Assert.assertEquals("enc2", method.getResponseHeader(HttpHeaderNames.CONTENT_ENCODING).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testGetEncoding3() throws IOException
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/encoding");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
      method.addRequestHeader(HttpHeaderNames.ACCEPT_ENCODING, "enc3");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         Assert.assertEquals("enc3", method.getResponseBodyAsString());
         Assert.assertEquals("enc3", method.getResponseHeader(HttpHeaderNames.CONTENT_ENCODING).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
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
