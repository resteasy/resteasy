package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseTest extends BaseResourceTest
{
   @Path("/")
   public static class Resource
   {
      public static final String ENTITY = "ENtiTy";

      @GET
      @Path("getentityannotations")
      public Response getEntityAnnotations() {
         Annotation[] annotations = ResponseFilter.class.getAnnotations();
         Response.ResponseBuilder builder = Response.ok();
         builder = builder.entity("entity", annotations);
         Response response = builder.build();
         return response;
      }

      @POST
      @Path("hasentity")
      public Response hasEntity(String entity) {
         Response.ResponseBuilder builder = Response.ok();
         if (entity != null && entity.length() != 0)
            builder = builder.entity(entity);
         Response response = builder.build();
         return response;
      }

      @GET
      @Path("empty")
      public Response empty()
      {
         return Response.ok().build();
      }

      @GET
      @Produces("text/plain")
      @Path("default_head")
      public Response defaultHead()
      {
         return Response.ok("f").build();
      }

      @HEAD
      @Path("head")
      public String head()
      {
         System.out.println("here!!");
         return "head";
      }

      @GET
      @Path("entity")
      @Produces(MediaType.TEXT_PLAIN)
      public String entity() {
         return ENTITY;
      }

      @GET
      @Path("date")
      public String date(@QueryParam("date") String date) {
         return date;
      }

      @POST
      @Path("link")
      public Response getLink(String rel) {
         Response.ResponseBuilder builder = Response.ok();
         if (rel != null && rel.length() != 0)
            builder.links(createLink("path", rel));
         return builder.build();
      }
      protected static Link createLink(String path, String rel) {
         return Link.fromUri(createUri(path)).rel(rel).build();
      }
      protected static URI createUri(String path) {
         URI uri;
         try {
            uri = new URI("http://localhost.tck:888/url404/" + path);
         } catch (URISyntaxException e) {
            throw new RuntimeException(e);
         }
         return uri;
      }

      @GET
      @Path("entitybodyresponsetest")
      public Response entityResponseTest() {
         RuntimeDelegate rd = RuntimeDelegate.getInstance();
         Response.ResponseBuilder rb = rd.createResponseBuilder();
         String rwe = "hello";
         Response build = rb.entity(rwe).build();
         return build;
      }

      @GET
      @Path("nullEntityResponse")
      public Response nullEntityResponse() {
         RuntimeDelegate rd = RuntimeDelegate.getInstance();
         Response.ResponseBuilder rb = rd.createResponseBuilder();
         return rb.entity(null).build();
      }




   }

   @Provider
   public static class DateReaderWriter implements MessageBodyReader<Date>,
           MessageBodyWriter<Date>
   {

      public static final int ANNOTATION_NONE = 0;
      public static final int ANNOTATION_CONSUMES = 1 << 2;
      public static final int ANNOTATION_PROVIDER = 1 << 3;
      public static final int ANNOTATION_UNKNOWN = 1 << 7;

      private AtomicInteger atom;

      public DateReaderWriter(AtomicInteger atom) {
         super();
         this.atom = atom;
      }

      @Override
      public long getSize(Date arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
                          MediaType arg4) {
         return String.valueOf(Long.MAX_VALUE).length();
      }

      @Override
      public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
                                 MediaType arg3) {
         return arg0 == Date.class;
      }

      @Override
      public void writeTo(Date date, Class<?> arg1, Type arg2, Annotation[] arg3,
                          MediaType arg4, MultivaluedMap<String, Object> arg5,
                          OutputStream stream) throws IOException, WebApplicationException
      {
         parseAnnotations(arg3);
         stream.write(String.valueOf(date.getTime()).getBytes());
      }

      @Override
      public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
                                MediaType arg3) {
         return isWriteable(arg0, arg1, arg2, arg3);
      }

      @Override
      public Date readFrom(Class<Date> arg0, Type arg1, Annotation[] arg2,
                           MediaType arg3, MultivaluedMap<String, String> arg4,
                           InputStream arg5) throws IOException, WebApplicationException {
         parseAnnotations(arg2);

         InputStreamReader reader = new InputStreamReader(arg5);
         BufferedReader br = new BufferedReader(reader);
         long date = Long.parseLong(br.readLine());
         return new Date(date);
      }

      protected void parseAnnotations(Annotation[] annotations) {
         int value = ANNOTATION_NONE;
         if (annotations != null)
            for (Annotation annotation : annotations)
               if (annotation.annotationType() == Consumes.class)
                  value |= ANNOTATION_CONSUMES;
               else if (annotation.annotationType() == Provider.class)
                  value |= ANNOTATION_PROVIDER;
               else
                  value |= ANNOTATION_UNKNOWN;
         atom.set(value);
      }
   }

   @Provider
   public static class ResponseFilter implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         if (requestContext.getUriInfo().getPath().contains("hasentity"))
         {
         System.out.println("HASENTITY: " + responseContext.hasEntity());
         responseContext.setEntity(String.valueOf(responseContext.hasEntity()), null, MediaType.TEXT_PLAIN_TYPE);
         }
         else if (requestContext.getUriInfo().getPath().contains("getentityannotations"))
         {
            for (Annotation annotation : responseContext.getEntityAnnotations())
            {
               System.out.println(annotation.annotationType().getName());
            }
         }
      }
   }


   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(ResponseFilter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      client.close();
   }

   @Test
   public void testHasEntity()
   {
      Response response = client.target(generateURL("/hasentity")).request("*/*").post(Entity.entity("entity", MediaType.WILDCARD_TYPE));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(response.getMediaType(), MediaType.TEXT_PLAIN_TYPE);
      response.close();

   }

   @Test
   public void testDefaultHead()
   {
      // mucks up stream so create our own client.
      //Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/default_head")).request().head();
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.getMediaType());
      response.close();
      //client.close();

   }


   @Test
   public void testHead()
   {
      // mucks up stream so create our own client.
      //Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/head")).request().head();
      Assert.assertEquals(200, response.getStatus());
      if (response.hasEntity())
      {
         String str = response.readEntity(String.class);
      }
      response.close();
      //client.close();

   }

   @Test
   public void testEmpty()
   {
      Response response = client.target(generateURL("/empty")).request().head();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertFalse(response.hasEntity());
      response.close();

   }

   @Test
   public void testEntityAnnotations()
   {
      Response response = client.target(generateURL("/getentityannotations")).request().get();
      Assert.assertEquals(200, response.getStatus());
      response.close();

   }



   @Test
   public void testNoStatus()
   {
      Response response = client.target(generateURL("/entitybodyresponsetest")).request().get();
      Assert.assertEquals(200, response.getStatus());
      response.close();

   }

   @Test
   public void testNullEntityNoStatus()
   {
      Response response = client.target(generateURL("/nullEntityResponse")).request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();

   }


   @Test
   public void hasLinkWhenLinkTest()
   {
      Response response = client.target(generateURL("/link")).request().post(Entity.text("path"));
      Assert.assertTrue(response.hasLink("path"));
      response.close();
   }


   protected String readLine(Reader reader) throws IOException {
      String line = null;
      BufferedReader buffered = new BufferedReader(reader);
      try {
         line = buffered.readLine();
      } catch (IOException e) {
         buffered.close();
         throw e;
      }
      return line;
   }

   protected <T> GenericType<T> generic(Class<T> clazz) {
      return new GenericType<T>(clazz);
   }

   @Provider
   @Consumes
/**
 * This is the dummy class to get annotations from it
 */
   public abstract class AnnotatedClass {

   }

   @Test
   public void readEntityGenericTypeAnnotationTest()
   {
      Date date = Calendar.getInstance().getTime();
      String sDate = String.valueOf(date.getTime());
      Annotation[] annotations = AnnotatedClass.class.getAnnotations();
      int expected = DateReaderWriter.ANNOTATION_CONSUMES
              | DateReaderWriter.ANNOTATION_PROVIDER;

      AtomicInteger ai = new AtomicInteger();
      DateReaderWriter drw = new DateReaderWriter(ai);

      Response response = client.target(generateURL("/date")).register(drw).queryParam("date", sDate).request().get();
      response.bufferEntity();

      Date entity = response.readEntity(generic(Date.class), annotations);
      System.out.println(entity.toString());
      Assert.assertTrue(date.equals(entity));

      Assert.assertTrue(ai.get() == expected);

      String responseDate = response.readEntity(generic(String.class),
              annotations);
      Assert.assertTrue(sDate.equals(responseDate));

      Assert.assertTrue(ai.get() == expected);
      response.close();

   }




   @Test
   public void readEntityGenericTypeTest() throws Exception
   {
      Response response = client.target(generateURL("/entity")).request()
              .get();
      Assert.assertEquals(200, response.getStatus());
      response.bufferEntity();
      String line;

      Reader reader = response.readEntity(new GenericType<Reader>(Reader.class));
      line = readLine(reader);
      Assert.assertTrue(Resource.ENTITY.equals(line));
      byte[] buffer = new byte[0];
      buffer = response.readEntity(generic(buffer.getClass()));
      Assert.assertNotNull(buffer);
      line = new String(buffer);
      Assert.assertTrue(Resource.ENTITY.equals(line));
      response.close();
   }


}
