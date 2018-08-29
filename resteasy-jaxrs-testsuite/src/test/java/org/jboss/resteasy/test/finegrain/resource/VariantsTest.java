package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
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
   private static final Logger LOG = Logger.getLogger(VariantsTest.class);
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
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
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("en", response.getEntity());
      Assert.assertEquals("en", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageWildcard() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "*");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      LOG.info(response.getEntity());
      Assert.assertNotNull(response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageSubLocal() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/brazil"));
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "pt");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      LOG.info(response.getEntity());
      Assert.assertNotNull(response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageZero() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "*,zh;q=0,en;q=0,fr;q=0");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
      LOG.info(response.getEntity());
   }

   @Test
   public void testGetLanguageZh() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "zh");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("zh", response.getEntity());
      Assert.assertEquals("zh", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetLanguageMultiple() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en;q=0.3, zh;q=0.4, fr");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("fr", response.getEntity());
      Assert.assertEquals("fr", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
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
      ClientRequest request = new ClientRequest(generateURL("/complex"));
      request.header(HttpHeaderNames.ACCEPT, "text/xml");
      request.header(HttpHeaderNames.ACCEPT, "application/xml");
      request.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      request.header(HttpHeaderNames.ACCEPT, "image/png");
      request.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      request.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      request.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en;q=0.5");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.getEntity());
      Assert.assertEquals("application/xml;charset=UTF-8", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_TYPE));
      Assert.assertEquals("en-us", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetComplex2() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/complex"));
      request.header(HttpHeaderNames.ACCEPT, "text/xml");
      request.header(HttpHeaderNames.ACCEPT, "application/xml");
      request.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      request.header(HttpHeaderNames.ACCEPT, "image/png");
      request.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      request.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      request.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en, en-us");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.getEntity());
      Assert.assertEquals("application/xml;charset=UTF-8", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_TYPE));
      Assert.assertEquals("en-us", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetComplex3() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/complex"));
      request.header(HttpHeaderNames.ACCEPT, "application/xml");
      request.header(HttpHeaderNames.ACCEPT, "text/xml");
      request.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      request.header(HttpHeaderNames.ACCEPT, "image/png");
      request.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      request.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      request.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en;q=0.5");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.getEntity());
      Assert.assertEquals("application/xml;charset=UTF-8", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_TYPE));
      Assert.assertEquals("en-us", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
   }

   @Test
   public void testGetComplex4() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/complex"));
      request.header(HttpHeaderNames.ACCEPT, "application/xml");
      request.header(HttpHeaderNames.ACCEPT, "text/xml");
      request.header(HttpHeaderNames.ACCEPT, "application/xhtml+xml");
      request.header(HttpHeaderNames.ACCEPT, "image/png");
      request.header(HttpHeaderNames.ACCEPT, "text/html;q=0.9");
      request.header(HttpHeaderNames.ACCEPT, "text/plain;q=0.8");
      request.header(HttpHeaderNames.ACCEPT, "*/*;q=0.5");
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en, en-us;q=0.5");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.getEntity());
      Assert.assertEquals("en", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_LANGUAGE));
      Assert.assertEquals("text/xml;charset=UTF-8", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_TYPE));
   }


   @Test
   public void testGetComplexNotAcceptable() throws Exception
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/complex"));
         request.header(HttpHeaderNames.ACCEPT, "application/atom+xml");
         request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "en-us, en");
         ClientResponse<?> response = request.get();
         Assert.assertEquals(406, response.getStatus());
         String vary = response.getResponseHeaders().getFirst(HttpHeaderNames.VARY);
         Assert.assertNotNull(vary);
         LOG.info("vary: " + vary);
         Assert.assertTrue(contains(vary, "Accept"));
         Assert.assertTrue(contains(vary, "Accept-Language"));
         response.releaseConnection();
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/complex"));
         request.header(HttpHeaderNames.ACCEPT, "application/xml");
         request.header(HttpHeaderNames.ACCEPT_LANGUAGE, "fr");
         ClientResponse<?> response = request.get();
         Assert.assertEquals(406, response.getStatus());
         String vary = response.getResponseHeaders().getFirst(HttpHeaderNames.VARY);
         Assert.assertNotNull(vary);
         LOG.info("vary: " + vary);
         Assert.assertTrue(contains(vary, "Accept"));
         Assert.assertTrue(contains(vary, "Accept-Language"));
         response.releaseConnection();
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
      ClientRequest request = new ClientRequest(generateURL("/encoding"));
      request.header(HttpHeaderNames.ACCEPT_ENCODING, "enc1");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("enc1", response.getEntity());
      Assert.assertEquals("enc1", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncoding2() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/encoding"));
      request.header(HttpHeaderNames.ACCEPT_ENCODING, "enc2");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("enc2", response.getEntity());
      Assert.assertEquals("enc2", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncoding3() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/encoding"));
      request.header(HttpHeaderNames.ACCEPT_ENCODING, "enc3");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("enc3", response.getEntity());
      Assert.assertEquals("enc3", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncodingQ() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/encoding"));
      request.header(HttpHeaderNames.ACCEPT_ENCODING, "enc1;q=0.5, enc2;q=0.9");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("enc2", response.getEntity());
      Assert.assertEquals("enc2", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_ENCODING));
   }

   @Test
   public void testGetEncodingQ2() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/encoding"));
      request.header(HttpHeaderNames.ACCEPT_ENCODING, "enc1;q=0, enc2;q=0.888, enc3;q=0.889");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("enc3", response.getEntity());
      Assert.assertEquals("enc3", response.getResponseHeaders().getFirst(HttpHeaderNames.CONTENT_ENCODING));
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
