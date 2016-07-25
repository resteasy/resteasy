package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.List;
import java.util.Locale;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VariantsTest
{
   private static Dispatcher dispatcher;
   private static Client client;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(LanguageVariantResource.class);
      dispatcher.getRegistry().addPerRequestResource(ComplexVariantResource.class);
      dispatcher.getRegistry().addPerRequestResource(EncodingVariantResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class LanguageVariantResource
   {
      @GET
      @Produces("text/plain")
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
      @Path("/brazil")
      @GET
      @Produces("text/plain")
      public Response doGetBrazil(@Context Request r)
      {
         List<Variant> vs = Variant.VariantListBuilder.newInstance().languages(new Locale("pt", "BR")).add().build();

         Variant v = r.selectVariant(vs);
         if (v == null)
            return Response.notAcceptable(vs).build();
         else
            return Response.ok(v.getLanguage(), v).build();
      }
   }

   @Test
   public void testGetLanguageEn() throws Exception
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("en", response.readEntity(String.class));
      Assert.assertEquals("en", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageWildcard() throws Exception
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "*");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      System.out.println(response.readEntity(String.class));
      Assert.assertNotNull(response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageSubLocal() throws Exception
   {
      Builder builder = client.target(generateURL("/brazil")).request();
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "pt");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      System.out.println(response.readEntity(String.class));
      Assert.assertNotNull(response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageZero() throws Exception
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "*,zh;q=0,en;q=0,fr;q=0");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
      System.out.println(response.readEntity(String.class));
   }

   @Test
   public void testGetLanguageZh() throws Exception
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "zh");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("zh", response.readEntity(String.class));
      Assert.assertEquals("zh", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageMultiple() throws Exception
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en;q=0.3, zh;q=0.4, fr");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("fr", response.readEntity(String.class));
      Assert.assertEquals("fr", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
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
   public void testGetComplex1() throws Exception
   {
      Builder builder = client.target(generateURL("/complex")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text/xml");
      builder.header(HttpHeaderNames.ACCEPT, "application/xml");
      builder.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      builder.header(HttpHeaderNames.ACCEPT, "image/png");
      builder.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      builder.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      builder.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en;q=0.5");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals("application/xml", response.getHeaderString(HttpHeaderNames.CONTENT_TYPE));
      Assert.assertEquals("en-us", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetComplex2() throws Exception
   {
      Builder builder = client.target(generateURL("/complex")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text/xml");
      builder.header(HttpHeaderNames.ACCEPT, "application/xml");
      builder.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      builder.header(HttpHeaderNames.ACCEPT, "image/png");
      builder.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      builder.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      builder.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en, en-us");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals("application/xml", response.getHeaderString(HttpHeaderNames.CONTENT_TYPE));
      Assert.assertEquals("en-us", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetComplex3() throws Exception
   {
      Builder builder = client.target(generateURL("/complex")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/xml");
      builder.header(HttpHeaderNames.ACCEPT, "text/xml");
      builder.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      builder.header(HttpHeaderNames.ACCEPT, "image/png");
      builder.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      builder.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      builder.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en;q=0.5");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals("application/xml", response.getHeaderString(HttpHeaderNames.CONTENT_TYPE));
      Assert.assertEquals("en-us", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetComplex4() throws Exception
   {
      Builder builder = client.target(generateURL("/complex")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/xml");
      builder.header(HttpHeaderNames.ACCEPT, "text/xml");
      builder.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      builder.header(HttpHeaderNames.ACCEPT, "image/png");
      builder.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      builder.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      builder.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en, en-us;q=0.5");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals("en", response.getHeaderString(HttpHeaderNames.CONTENT_LANGUAGE));
      Assert.assertEquals("text/xml", response.getHeaderString(HttpHeaderNames.CONTENT_TYPE));
   }


   @Test
   public void testGetComplexNotAcceptable() throws Exception
   {
      {
         Builder builder = client.target(generateURL("/complex")).request();
         builder.header(HttpHeaderNames.ACCEPT, "application/atom+xml");
         builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en");
         Response response = builder.get();
         Assert.assertEquals(406, response.getStatus());;
         String vary = response.getHeaderString(HttpHeaderNames.VARY);
         Assert.assertNotNull(vary);
         System.out.println("vary: " + vary);
         Assert.assertTrue(contains(vary, "Accept"));
         Assert.assertTrue(contains(vary, "Accept-Language"));
         response.close();
      }

      {
         Builder builder = client.target(generateURL("/complex")).request();
         builder.header(HttpHeaderNames.ACCEPT, "application/xml");
         builder.header(HttpHeaderNames.ACCEPT_LANGUAGE, "fr");
         Response response = builder.get();
         Assert.assertEquals(406, response.getStatus());;
         String vary = response.getHeaderString(HttpHeaderNames.VARY);
         Assert.assertNotNull(vary);
         System.out.println("vary: " + vary);
         Assert.assertTrue(contains(vary, "Accept"));
         Assert.assertTrue(contains(vary, "Accept-Language"));
         response.close();
      }
   }

   @Path("/encoding")
   public static class EncodingVariantResource
   {
      @GET
      public Response doGet(@Context Request r)
      {
         List<Variant> vs = Variant.VariantListBuilder.newInstance().encodings("enc1", "enc2", "enc3").add().build();
         Variant v = r.selectVariant(vs);
         if (v == null)
            return Response.notAcceptable(vs).build();
         else
            return Response.ok(v.getEncoding(), v).build();
      }
   }

   @Test
   public void testGetEncoding1() throws Exception
   {
      Builder builder = client.target(generateURL("/encoding")).request();
      builder.header(HttpHeaderNames.ACCEPT_ENCODING, "enc1");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc1", response.readEntity(String.class));
      Assert.assertEquals("enc1", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncoding2() throws Exception
   {
      Builder builder = client.target(generateURL("/encoding")).request();
      builder.header(HttpHeaderNames.ACCEPT_ENCODING, "enc2");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc2", response.readEntity(String.class));
      Assert.assertEquals("enc2", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncoding3() throws Exception
   {
      Builder builder = client.target(generateURL("/encoding")).request();
      builder.header(HttpHeaderNames.ACCEPT_ENCODING, "enc3");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc3", response.readEntity(String.class));
      Assert.assertEquals("enc3", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncodingQ() throws Exception
   {
      Builder builder = client.target(generateURL("/encoding")).request();
      builder.header(HttpHeaderNames.ACCEPT_ENCODING, "enc1;q=0.5, enc2;q=0.9");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc2", response.readEntity(String.class));
      Assert.assertEquals("enc2", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncodingQ2() throws Exception
   {
      Builder builder = client.target(generateURL("/encoding")).request();
      builder.header(HttpHeaderNames.ACCEPT_ENCODING, "enc1;q=0, enc2;q=0.888, enc3;q=0.889");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc3", response.readEntity(String.class));
      Assert.assertEquals("enc3", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
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
