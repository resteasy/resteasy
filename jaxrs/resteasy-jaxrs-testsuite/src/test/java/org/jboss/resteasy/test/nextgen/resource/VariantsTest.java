package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VariantsTest extends BaseResourceTest
{
   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(LanguageVariantResource.class);
      addPerRequestResource(ComplexVariantResource.class);
      addPerRequestResource(EncodingVariantResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      client.close();
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

      @GET
      @Path("/SelectVariantTestResponse")
      public Response selectVariantTestResponse(@Context Request req) {
         List<Variant> list = Variant.encodings("CP1250", "UTF-8")
                 .languages(Locale.ENGLISH)
                 .mediaTypes(MediaType.APPLICATION_JSON_TYPE).add().build();
         Variant selectedVariant = req.selectVariant(list);
         if (null == selectedVariant)
            return Response.notAcceptable(list).build();
         return Response.ok("entity").build();
      }

      @GET
      @Path("/SelectVariantTestGet")
      public Response selectVariantTestGet(@Context Request req) {
         List<Variant> vs = null;

         try {
            req.selectVariant(vs);
            return Response.ok("Test FAILED - no exception thrown").build();
         } catch (IllegalArgumentException ile) {
            return Response.ok("PASSED")
                    .build();
         } catch (Throwable th) {
            th.printStackTrace();
            return Response.ok(
                    "Test FAILED - wrong type exception thrown" +
                            th.getMessage()).build();
         }
      }

      @PUT
      @Path("/SelectVariantTestPut")
      public Response selectVariantTestPut(@Context Request req) {
         return selectVariantTestGet(req);
      }

      @POST
      @Path("/SelectVariantTestPost")
      public Response selectVariantTestPost(@Context Request req) {
         return selectVariantTestGet(req);
      }

      @DELETE
      @Path("/SelectVariantTestDelete")
      public Response selectVariantTestDelete(@Context Request req) {
         return selectVariantTestGet(req);
      }

      @GET
      @Path("/preconditionsSimpleGet")
      public Response evaluatePreconditionsEntityTagGetSimpleTest(
              @Context Request req) {
         boolean ok = evaluatePreconditionsEntityTag(req, "AAA");
         if (!ok)
            return Response.status(Response.Status.GONE).build();
         ok &= evaluatePreconditionsNowEntityTagNull(req);
         if (!ok)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
         ok &= evaluatePreconditionsEntityTagNull(req);
         return createResponse(ok);
      }
      private static boolean evaluatePreconditionsEntityTagNull(Request req) {
         try {
            req.evaluatePreconditions((EntityTag) null);
            return false;
         } catch (IllegalArgumentException iae) {
            return true;
         }
      }

      private static boolean evaluatePreconditionsNowEntityTagNull(Request req) {
         try {
            Date now = Calendar.getInstance().getTime();
            req.evaluatePreconditions(now, (EntityTag) null);
            return false;
         } catch (IllegalArgumentException iae) {
            return true;
         }
      }

      private static EntityTag createTag(String tag) {
         String xtag = new StringBuilder().append("\"").append(tag).append("\"")
                 .toString();
         return EntityTag.valueOf(xtag);
      }

      private static boolean evaluatePreconditionsEntityTag(Request req, String tag) {
         Response.ResponseBuilder rb = req.evaluatePreconditions(createTag(tag));
         return rb == null;
      }

      private static Response createResponse(boolean ok) {
         Response.Status status = ok ? Response.Status.OK : Response.Status.PRECONDITION_FAILED;
         return Response.status(status).build();
      }




   }

   @Test
   public void evaluatePreconditionsTagNullAndSimpleGetTest()
   {
      Response response = client.target(generateURL("/preconditionsSimpleGet")).request()
              .get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }


   @Test
   public void selectVariantPutRequestTest()
   {
      Response response = client.target(generateURL("/SelectVariantTestPut")).request()
              .put(null);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("PASSED", response.readEntity(String.class));
      response.close();
  }


   @Test
   public void selectVariantResponseVaryTest()
   {
      Response response = client.target(generateURL("/SelectVariantTestResponse")).request()
              .accept("application/json")
              .acceptEncoding("*").get();
      Assert.assertEquals(200, response.getStatus());
      List<String> headers = response.getStringHeaders().get("Vary");
      Assert.assertEquals(1, headers.size());
      String vary = headers.get(0);
      System.out.println(vary);
      Assert.assertTrue(vary.contains("Accept-Language"));
      Assert.assertTrue(vary.contains("Accept-Encoding"));
      Assert.assertTrue(vary.matches(".*Accept.*Accept.*Accept.*"));
      response.close();
   }


   @Test
   public void testGetLanguageEn() throws Exception
   {
      Response response = client.target(generateURL("/")).request().acceptLanguage("en").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("en", response.readEntity(String.class));
      Assert.assertEquals("en", response.getLanguage().toString());
      response.close();
   }

   @Test
   public void testGetLanguageWildcard() throws Exception
   {
      Response response = client.target(generateURL("/")).request().acceptLanguage("*").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertNotNull(response.getLanguage());
      response.close();
   }

   @Test
   public void testGetLanguageSubLocal() throws Exception
   {
      Response response = client.target(generateURL("/brazil")).request()
              .acceptLanguage("pt").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertNotNull(response.getLanguage());
      response.close();
   }

   @Test
   public void testGetLanguageZero() throws Exception
   {
      Response response = client.target(generateURL("/")).request()
              .acceptLanguage("*", "zh;q=0", "en;q=0" ,"fr;q=0").get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
      response.close();
   }

   @Test
   public void testGetLanguageZh() throws Exception
   {
      Response response = client.target(generateURL("/")).request().acceptLanguage("zh").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("zh", response.readEntity(String.class));
      Assert.assertEquals("zh", response.getLanguage().toString());
      response.close();
   }

   @Test
   public void testGetLanguageMultiple() throws Exception
   {
      Response response = client.target(generateURL("/")).request()
              .acceptLanguage("en;q=0.3", "zh;q=0.4",  "fr").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("fr", response.readEntity(String.class));
      Assert.assertEquals("fr", response.getLanguage().toString());
      response.close();
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
      Response response = client.target(generateURL("/complex")).request()
            .accept("text/xml", "application/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
            .acceptLanguage("en-us", "en;q=0.5").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getMediaType());
      Assert.assertEquals("en-us", new LocaleDelegate().toString(response.getLanguage()));
      response.close();
   }

   @Test
   public void testGetComplex2() throws Exception
   {
      Response response = client.target(generateURL("/complex")).request()
              .accept("text/xml", "application/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
              .acceptLanguage("en", "en-us").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getMediaType());
      Assert.assertEquals("en-us", new LocaleDelegate().toString(response.getLanguage()));
      response.close();
   }

   @Test
   public void testGetComplex3() throws Exception
   {
      Response response = client.target(generateURL("/complex")).request()
              .accept("application/xml", "text/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
              .acceptLanguage("en-us", "en;q=0.5").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getMediaType());
      Assert.assertEquals("en-us", new LocaleDelegate().toString(response.getLanguage()));
      response.close();
   }

   @Test
   public void testGetComplex4() throws Exception
   {
      Response response = client.target(generateURL("/complex")).request()
              .accept("application/xml", "text/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
              .acceptLanguage("en",  "en-us;q=0.5").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("GET", response.readEntity(String.class));
      Assert.assertEquals("en", response.getLanguage().toString());
      Assert.assertEquals(MediaType.TEXT_XML_TYPE, response.getMediaType());
      response.close();
   }


   @Test
   public void testGetComplexNotAcceptable() throws Exception
   {
      {
         Response response = client.target(generateURL("/complex")).request()
                 .accept("application/atom+xml")
                 .acceptLanguage("en-us", "en").get();
         Assert.assertEquals(406, response.getStatus());;
         String vary = response.getHeaderString(HttpHeaderNames.VARY);
         Assert.assertNotNull(vary);
         System.out.println("vary: " + vary);
         Assert.assertTrue(contains(vary, "Accept"));
         Assert.assertTrue(contains(vary, "Accept-Language"));
         response.close();
      }

      {
         Response response = client.target(generateURL("/complex")).request()
                 .accept("application/xml")
                 .acceptLanguage("fr").get();
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
      Response response = client.target(generateURL("/encoding")).request()
              .acceptEncoding("enc1").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc1", response.readEntity(String.class));
      Assert.assertEquals("enc1", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
      response.close();
   }

   @Test
   public void testGetEncoding2() throws Exception
   {
      Response response = client.target(generateURL("/encoding")).request()
              .acceptEncoding("enc2").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc2", response.readEntity(String.class));
      Assert.assertEquals("enc2", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
      response.close();
   }

   @Test
   public void testGetEncoding3() throws Exception
   {
      Response response = client.target(generateURL("/encoding")).request()
              .acceptEncoding("enc3").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc3", response.readEntity(String.class));
      Assert.assertEquals("enc3", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
      response.close();
   }

   @Test
   public void testGetEncodingQ() throws Exception
   {
      Response response = client.target(generateURL("/encoding")).request()
              .acceptEncoding("enc1;q=0.5", "enc2;q=0.9").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc2", response.readEntity(String.class));
      Assert.assertEquals("enc2", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
      response.close();
   }

   @Test
   public void testGetEncodingQ2() throws Exception
   {
      Response response = client.target(generateURL("/encoding")).request()
              .acceptEncoding("enc1;q=0", "enc2;q=0.888", "enc3;q=0.889").get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());;
      Assert.assertEquals("enc3", response.readEntity(String.class));
      Assert.assertEquals("enc3", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
      response.close();
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
