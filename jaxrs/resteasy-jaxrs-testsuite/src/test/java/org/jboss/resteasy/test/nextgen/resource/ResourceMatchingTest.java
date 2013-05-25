package org.jboss.resteasy.test.nextgen.resource;

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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMatchingTest extends BaseResourceTest
{
   @Path("yas")
   public static class YetAnotherSubresource {
      @GET
      @Produces("text/*")
      public String getTextStar() {
         return "text/*";
      }

      @POST
      @Consumes("text/*")
      public String postTextStar() {
         return "text/*";
      }

      @POST
      @Consumes("text/xml;qs=0.7")
      public String xml() {
         return MediaType.TEXT_XML;
      }

      @GET
      @Produces("text/xml;qs=0.7")
      public String xmlGet() {
         return MediaType.TEXT_XML;
      }

      @GET
      @Produces("application/xml;qs=0.8")
      public String appXmlGet() {
         return MediaType.APPLICATION_XML;
      }

      @GET
      @Produces("testiii/textiii;qs=0.7")
      public String testiiiTextiiiGet() {
         return "testiii/textiii";
      }


      @GET
      @Produces("testi/*")
      public String testStar(){
         return "test/*";
      }

      @GET
      @Produces("testi/text")
      public String testText(){
         return "test/text";
      }

      @GET
      @Produces("testii/texta")
      public String testIITextA(){
         return "textA";
      }

      @GET
      @Produces("testii/textb")
      public String testIITextB(){
         return "textB";
      }

   }
   public static class AnotherResourceLocator {

      @GET
      public String get() {
         return getClass().getSimpleName();
      }

      @POST
      @Consumes(MediaType.TEXT_PLAIN)
      @Produces(MediaType.TEXT_PLAIN)
      public String post() {
         return get();
      }

      @DELETE
      public String delete() {
         return get();
      }
   }



   @Path("resource/subresource")
   public static class MainSubResource {
      public static final String ID = "subresource";

      @GET
      public String subresource() {
         return this.getClass().getSimpleName();
      }

      @POST
      @Path("sub")
      @Consumes(MediaType.TEXT_PLAIN)
      @Produces(MediaType.TEXT_PLAIN)
      public String sub() {
         return this.getClass().getSimpleName();
      }

      @GET
      @Path("{id}")
      public String neverHere() {
         return ID;
      }

      @POST
      @Path("consumes")
      @Consumes(MediaType.TEXT_PLAIN)
      public String consumes() {
         return getClass().getSimpleName();
      }

      @Path("consumeslocator")
      public AnotherResourceLocator consumeslocator() {
         return new AnotherResourceLocator();
      }

      @POST
      @Path("produces")
      @Produces(MediaType.TEXT_PLAIN)
      public String produces() {
         return getClass().getSimpleName();
      }

      @Path("produceslocator")
      public AnotherResourceLocator produceslocator() {
         return new AnotherResourceLocator();
      }

   }

   @Path("resource/subresource/sub")
   public static class AnotherSubResource {

      @POST
      @Consumes(MediaType.TEXT_PLAIN)
      public String sub() {
         return getClass().getSimpleName();
      }

      @POST
      public String subsub() {
         return sub() + sub();
      }

      @GET
      public String get() {
         return sub();
      }

      @GET
      @Produces(MediaType.TEXT_PLAIN)
      public String getget() {
         return subsub();
      }

      @GET
      @Produces("text/*")
      public String getTextStar() {
         return "text/*";
      }

      @POST
      @Consumes("text/*")
      public String postTextStar() {
         return "text/*";
      }
   }

   @Path("weight")
   public static class WeightResource {

      @POST
      @Produces("text/plain;qs=0.9")
      public String plain() {
         return MediaType.TEXT_PLAIN;
      }

      @POST
      @Produces("text/html;qs=0.8")
      public String html(@Context Request req) {
         return MediaType.TEXT_HTML;
      }

      @POST
      @Produces("text/xml;qs=0.5")
      public String xml() {
         return MediaType.TEXT_XML;
      }

      @POST
      @Produces("application/*;qs=0.5")
      public String app() {
         return MediaType.WILDCARD;
      }

      @POST
      @Produces("application/xml;qs=0.5")
      public String appxml() {
         return MediaType.APPLICATION_XML;
      }


      @POST
      @Produces("image/png;qs=0.6")
      public String png() {
         return "image/png";
      }

      @POST
      @Produces("image/*;qs=0.7")
      public String image() {
         return "image/any";
      }

      @POST
      @Produces("*/*;qs=0.1")
      public String any() {
         return MediaType.WILDCARD;
      }

   }





   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(YetAnotherSubresource.class);
      addPerRequestResource(MainSubResource.class);
      addPerRequestResource(AnotherSubResource.class);
      addPerRequestResource(WeightResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testWildcard()
   {
      Response response = client.target(generateURL("/yas")).request("testi/*").get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals("test/text", response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testQS()
   {
      Response response = client.target(generateURL("/yas")).request("testiii/textiii", "application/xml").get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals("application/xml", response.readEntity(String.class));
      response.close();
   }


   @Test
   public void testOverride()
   {
      String clazz = AnotherSubResource.class.getSimpleName();
      Response response = client.target(generateURL("/resource/subresource/sub")).request().header("Content-Type", "text/plain").post(null);
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals("AnotherSubResource", response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testOptions()
   {
      Response response = client.target(generateURL("/resource/subresource/something")).request().options();
      Assert.assertEquals(response.getStatus(), 200);
      String actual = response.readEntity(String.class);
      Assert.assertTrue(actual.contains("GET"));
      response.close();
   }

   @Test
   public void testAvoidWildcard()
   {
      Response response = client.target(generateURL("/weight")).request("application/*;q=0.9", "application/xml;q=0.1").post(null);
      Assert.assertEquals(response.getStatus(), 200);
      MediaType mediaType = response.getMediaType();
      String actual = response.readEntity(String.class);
      Assert.assertEquals("application/xml", actual);
      Assert.assertEquals("application/xml", mediaType.toString());
      response.close();
   }




}
