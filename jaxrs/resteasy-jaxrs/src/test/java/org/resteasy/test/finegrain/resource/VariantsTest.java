package org.resteasy.test.finegrain.resource;

import org.junit.BeforeClass;
import org.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VariantsTest
{
   @BeforeClass
   public static void start()
   {
      ResteasyProviderFactory.initializeInstance();
   }

//   private static HttpServletDispatcher dispatcher;
//
//   @BeforeClass
//   public static void before() throws Exception
//   {
//      dispatcher = EmbeddedServletContainer.start();
//      dispatcher.getRegistry().addResource(LanguageVariantResource.class);
//      dispatcher.getRegistry().addResource(ComplexVariantResource.class);
//   }
//
//   @AfterClass
//   public static void after() throws Exception
//   {
//      EmbeddedServletContainer.stop();
//   }
//
//   @Path("/")
//   public static class LanguageVariantResource
//   {
//      @GET
//      public Response doGet(@Context Request r)
//      {
//         List<Variant> vs = Variant.VariantListBuilder.newInstance().
//                 languages("zh").
//                 languages("fr").
//                 languages("en").add().
//                 build();
//
//         Variant v = r.selectVariant(vs);
//         if (v == null)
//            return Response.notAcceptable(vs).build();
//         else
//            return Response.ok(v.getLanguage(), v).build();
//      }
//   }
//
//   public void testGetLanguageEn() throws IOException
//   {
//      initiateWebApplication(LanguageVariantResource.class);
//      WebResource rp = resource("/");
//
//      ClientResponse r = rp.
//              header("Accept-Language", "en").
//              get(ClientResponse.class);
//      assertEquals("en", r.getEntity(String.class));
//      assertEquals("en", r.getLanguage());
//   }
//
//   public void testGetLanguageZh() throws IOException
//   {
//      initiateWebApplication(LanguageVariantResource.class);
//      WebResource rp = resource("/");
//
//      ClientResponse r = rp.
//              header("Accept-Language", "zh").
//              get(ClientResponse.class);
//      assertEquals("zh", r.getEntity(String.class));
//      assertEquals("zh", r.getLanguage());
//   }
//
//   public void testGetLanguageMultiple() throws IOException
//   {
//      initiateWebApplication(LanguageVariantResource.class);
//      WebResource rp = resource("/");
//
//      ClientResponse r = rp.
//              header("Accept-Language", "en;q=0.3, zh;q=0.4, fr").
//              get(ClientResponse.class);
//      assertEquals("fr", r.getEntity(String.class));
//      assertEquals("fr", r.getLanguage());
//   }
//
//   @Path("/")
//   public static class ComplexVariantResource
//   {
//      @GET
//      public Response doGet(@Context Request r)
//      {
//         List<Variant> vs = Variant.VariantListBuilder.newInstance().
//                 mediaTypes(MediaType.parse("image/jpeg")).add().
//                 mediaTypes(MediaType.parse("application/xml")).languages("en-us").add().
//                 mediaTypes(MediaType.parse("text/xml")).languages("en").add().
//                 mediaTypes(MediaType.parse("text/xml")).languages("en-us").add().
//                 build();
//
//         Variant v = r.selectVariant(vs);
//         if (v == null)
//            return Response.notAcceptable(vs).build();
//         else
//            return Response.ok("GET", v).build();
//      }
//   }
//
//   public void testGetComplex1() throws IOException
//   {
//      initiateWebApplication(ComplexVariantResource.class);
//      WebResource rp = resource("/");
//
//      ClientResponse r = rp.accept("text/xml",
//              "application/xml",
//              "application/xhtml+xml",
//              "image/png",
//              "text/html;q=0.9",
//              "text/plain;q=0.8",
//              "*/*;q=0.5").
//              header("Accept-Language", "en-us,en;q=0.5").
//              get(ClientResponse.class);
//      assertEquals("GET", r.getEntity(String.class));
//      assertEquals(MediaType.parse("text/xml"), r.getType());
//      assertEquals("en-us", r.getLanguage());
//   }
//
//   public void testGetComplex2() throws IOException
//   {
//      initiateWebApplication(ComplexVariantResource.class);
//      WebResource rp = resource("/");
//
//      ClientResponse r = rp.accept("text/xml",
//              "application/xml",
//              "application/xhtml+xml",
//              "image/png",
//              "text/html;q=0.9",
//              "text/plain;q=0.8",
//              "*/*;q=0.5").
//              header("Accept-Language", "en,en-us").
//              get(ClientResponse.class);
//      assertEquals("GET", r.getEntity(String.class));
//      assertEquals(MediaType.parse("text/xml"), r.getType());
//      assertEquals("en", r.getLanguage());
//   }
//
//   public void testGetComplex3() throws IOException
//   {
//      initiateWebApplication(ComplexVariantResource.class);
//      WebResource rp = resource("/");
//
//      ClientResponse r = rp.accept("application/xml",
//              "text/xml",
//              "application/xhtml+xml",
//              "image/png",
//              "text/html;q=0.9",
//              "text/plain;q=0.8",
//              "*/*;q=0.5").
//              header("Accept-Language", "en-us,en;q=0.5").
//              get(ClientResponse.class);
//      assertEquals("GET", r.getEntity(String.class));
//      assertEquals(MediaType.parse("application/xml"), r.getType());
//      assertEquals("en-us", r.getLanguage());
//   }
//
//   public void testGetComplexNotAcceptable() throws IOException
//   {
//      initiateWebApplication(ComplexVariantResource.class);
//      WebResource rp = resource("/", false);
//
//      ClientResponse r = rp.accept("application/atom+xml").
//              header("Accept-Language", "en-us,en").
//              get(ClientResponse.class);
//      String vary = r.getMetadata().getFirst("Vary");
//      assertNotNull(vary);
//      assertTrue(contains(vary, "Accept"));
//      assertTrue(contains(vary, "Accept-Language"));
//      assertEquals(406, r.getStatus());
//
//      r = rp.accept("application/xml").
//              header("Accept-Language", "fr").
//              get(ClientResponse.class);
//      assertTrue(contains(vary, "Accept"));
//      assertTrue(contains(vary, "Accept-Language"));
//      assertEquals(406, r.getStatus());
//   }
//
//   private boolean contains(String l, String v)
//   {
//      String[] vs = l.split(",");
//      for (String s : vs)
//      {
//         s = s.trim();
//         if (s.equalsIgnoreCase(v))
//            return true;
//      }
//
//      return false;
//   }
}
