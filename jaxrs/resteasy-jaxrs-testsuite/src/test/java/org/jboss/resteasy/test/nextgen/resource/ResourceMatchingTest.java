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

   @Path("error")
   public static class ErrorResource {
      @GET
      @Produces("text/*")
      public String test(){
         return getClass().getSimpleName();
      }

      @POST
      @Produces("text/*")
      public Response response(String msg){
         return Response.ok(msg).build();
      }
   }


   public static class StringBean {
      private String header;

      public String get() {
         return header;
      }

      public void set(String header) {
         this.header = header;
      }

      @Override
      public String toString() {
         return "StringBean. To get a value, use rather #get() method.";
      }

      public StringBean(String header) {
         super();
         this.header = header;
      }
   }
   public static final//
   String readFromStream(InputStream stream) throws IOException {
      InputStreamReader isr = new InputStreamReader(stream);
      return readFromReader(isr);
   }

   public static final//
   String readFromReader(Reader reader) throws IOException {
      BufferedReader br = new BufferedReader(reader);
      String entity = br.readLine();
      br.close();
      return entity;
   }


   @Provider
   public static class StringBeanEntityProvider implements MessageBodyReader<StringBean>,
           MessageBodyWriter<StringBean>
   {

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return StringBean.class.isAssignableFrom(type);
      }

      @Override
      public long getSize(StringBean t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType) {
         return t.get().length();
      }

      @Override
      public void writeTo(StringBean t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException
      {
         entityStream.write(t.get().getBytes());
      }

      @Override
      public boolean isReadable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType) {
         return isWriteable(type, genericType, annotations, mediaType);
      }

      @Override
      public StringBean readFrom(Class<StringBean> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
              throws IOException, WebApplicationException {
         String stream = readFromStream(entityStream);
         StringBean bean = new StringBean(stream);
         return bean;
      }

   }


   @Path("nomedia")
   public static class NoMediaResource {

      @GET
      @Path("list")
      public List<String> serializable() {
         return java.util.Collections.singletonList("AA");
      }

      @GET
      @Path("responseoverride")
      public Response overrideNoProduces() {
         return Response.ok("<a>responseoverride</a>")
                 .type(MediaType.APPLICATION_XML_TYPE).build();
      }

      @GET
      @Path("nothing")
      public StringBean nothing() {
         return new StringBean("nothing");
      }

      @GET
      @Path("response")
      public Response response() {
         return Response.ok(nothing()).build();
      }

   }

   @Provider
   @Produces(MediaType.APPLICATION_SVG_XML)
   public static class MediaWriter implements MessageBodyWriter<List<?>> {

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return List.class.isAssignableFrom(type);
      }

      @Override
      public long getSize(List<?> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType) {
         return List.class.getSimpleName().length();
      }

      @Override
      public void writeTo(List<?> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException {
         entityStream.write(List.class.getSimpleName().getBytes());
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
      addPerRequestResource(ErrorResource.class);
      addPerRequestResource(NoMediaResource.class);
      deployment.getProviderFactory().register(StringBeanEntityProvider.class);
      deployment.getProviderFactory().register(MediaWriter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testBounded()
   {
      ResourceMethodRegistry registry = (ResourceMethodRegistry)deployment.getRegistry();
      for (Map.Entry<String, List<ResourceInvoker>> entry : registry.getBounded().entrySet())
      {
         List<ResourceInvoker> invokers = entry.getValue();

      }
   }

   @Test
   public void testMediaTypeFromProvider()
   {
      Response response = client.target(generateURL("/nomedia/list")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(MediaType.APPLICATION_SVG_XML_TYPE, response.getMediaType());
      response.close();

   }


   @Test
   public void testNoProduces()
   {
      Response response = client.target(generateURL("/nomedia/nothing")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE, response.getMediaType());
      response.close();

   }


   @Test
   public void testNonConcreteMatch()
   {
      Response response = client.target(generateURL("/error")).request("text/*").get();
      Assert.assertEquals(response.getStatus(), 406);
      response.close();

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
