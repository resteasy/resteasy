package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.TypeConverter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegressionTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
   }

   @AfterClass
   public static void after() throws Exception
   {
   }

   public static class Customer
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/simple")
      @GET
      public Response get()
      {
         Response.ResponseBuilder builder = Response.ok("hello world".getBytes());
         builder.header("CoNtEnT-type", "text/plain");
         return builder.build();
      }

      @Path("/string")
      @GET
      public Response getString()
      {
         Response.ResponseBuilder builder = Response.ok("hello world");
         builder.header("CoNtEnT-type", "text/plain");
         return builder.build();         
      }
      
      @Path("/complex")
      @GET
      public Object getComplex()
      {
         Response.ResponseBuilder builder = Response.status(HttpResponseCodes.SC_FOUND)
                 .entity("hello world".getBytes());
         builder.header("CoNtEnT-type", "text/plain");
         return builder.build();
      }

      @Path("/implicit")
      @GET
      @Produces("application/xml")
      public Object getCustomer()
      {
         System.out.println("GET CUSTOEMR");
         Customer cust = new Customer();
         cust.setName("bill");
         return Response.ok(cust).build();
      }

      @Path("/implicit")
      @DELETE
      public Object deleteCustomer()
      {
         return Response.ok().build();
      }

      @Path("/complex")
      @DELETE
      public void deleteComplex()
      {

      }

   }

   @Path("/")
   public interface SimpleClient
   {
      @Path("/implicit")
      @DELETE
      ClientResponse<String> deleteCustomer();

      @Path("/complex")
      @DELETE
      ClientResponse<String> deleteComplex();
   }

   @Provider
   @Produces("application/xml")
   public static class CustomerWriter implements MessageBodyWriter<Customer>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Customer.class);
      }

      public long getSize(Customer customer, Class<?> type, Type genericType, Annotation[] annotations,
                          MediaType mediaType)
      {
         return -1;
      }

      public void writeTo(Customer customer, Class<?> type, Type genericType, Annotation[] annotations,
                          MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
              throws IOException, WebApplicationException
      {
         String out = "<customer><name>" + customer.getName() + "</name></customer>";
         entityStream.write(out.getBytes());
      }
   }

   /**
    * Test JIRA bugs RESTEASY-144
    *
    * @throws Exception
    */
   @Test
   public void test144() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getProviderFactory().addMessageBodyWriter(CustomerWriter.class);
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      ClientResponse<?> response = null;
      try
      {
         ClientRequest request = new ClientRequest(generateURL("/implicit"));
         response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("application/xml", response.getHeaders().getFirst("content-type"));
         String s = new String(response.getEntity(byte[].class), "US-ASCII");
         Assert.assertEquals("<customer><name>bill</name></customer>", s);

         response = request.delete();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.releaseConnection();

         SimpleClient proxy = ProxyFactory.create(SimpleClient.class, generateBaseUrl());
         response = proxy.deleteCustomer();
         response.releaseConnection();

         response = proxy.deleteComplex();
         Assert.assertEquals(204, response.getStatus());
         response.releaseConnection();
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   /**
    * Test JIRA bugs RESTEASY-1 and RESTEASY-2
    *
    * @throws Exception
    */
   @Test
   public void test1and2() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      try
      {
         ClientRequest request = new ClientRequest(generateURL("/simple"));
         ClientResponse<byte[]> response = request.get(byte[].class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("text/plain", response.getHeaders().getFirst("content-type"));
         String s = new String(response.getEntity(), "US-ASCII");
         Assert.assertEquals("hello world", s);
         EmbeddedContainer.stop();
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
   
   @Test
   public void test1New() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setRegisterBuiltin(false);
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      factory.addMessageBodyWriter(new DefaultTextPlain());
      factory.addMessageBodyWriter(new TestReaderWriter());
      factory.addMessageBodyReader(new TestReaderWriter());
      deployment.setProviderFactory(factory);
      EmbeddedContainer.start(deployment);
      dispatcher = deployment.getDispatcher();      
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);

      try
      {
         ClientRequest request = new ClientRequest(generateURL("/string"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("text/plain", response.getHeaders().getFirst("content-type"));
         Assert.assertEquals("hello world", response.getEntity());
         Assert.assertTrue(TestReaderWriter.used);
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   @Provider
   @Produces("*/*")
   @Consumes("*/*")
   public static class TestReaderWriter implements MessageBodyWriter, MessageBodyReader
   {
      public static boolean used;
      
      public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }
      public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return o.toString().getBytes().length;
      }
      public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
         entityStream.write(o.toString().getBytes());
         used = true;
      }
      public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }
      public Object readFrom(Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException
      {
         String value = ProviderHelper.readString(entityStream, mediaType);
         return TypeConverter.getType(type, value);
      }
   }
   
   /**
    * Test JIRA bugs RESTEASY-61
    *
    * @throws Exception
    */
   @Test
   public void testJdkURLConnection() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      try
      {
         URL url = createURL("/simple");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         @SuppressWarnings("unused")
         Object obj = conn.getContent();
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   /**
    * Test JIRA bug RESTEASY-24 and 139
    */
   @Test
   public void test24() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      try
      {
         ClientRequest request = new ClientRequest(generateURL("/complex"));
         ClientResponse<byte[]> response = request.get(byte[].class);
         Assert.assertEquals(HttpResponseCodes.SC_FOUND, response.getStatus());
         Assert.assertEquals(response.getHeaders().getFirst("content-type"), "text/plain");
         byte[] responseBody = response.getEntity();
         String responseString = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", responseString);
      }
      finally
      {
         EmbeddedContainer.stop();
      }

   }

   @Path("/nowhere")
   public static interface NowhereClient
   {
      @GET
      @Produces("text/plain")
      public ClientResponse<String> read();
   }

   /**
    * Test JIRA bug RESTEASY-
    */
   @Test
   public void testIt() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         NowhereClient client = ProxyFactory.create(NowhereClient.class, generateBaseUrl());
         client.read();
      }
      finally
      {
         EmbeddedContainer.stop();
      }

   }

   @Path("/spaces")
   public static class Spaces
   {

      @Path("/with spaces")
      public Sub sub()
      {
         return new Sub();
      }
   }

   public static class Sub
   {
      @Path("/without")
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello";
      }
   }

   /**
    * Test JIRA bug RESTEASY-212
    */
   @Test
   public void test212() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Spaces.class);
      try
      {
         ClientRequest request = new ClientRequest(generateURL("/spaces/with%20spaces/without"));
         ClientResponse<?> response = request.get();
         Assert.assertEquals(200, response.getStatus());
         response.releaseConnection();
      }
      finally
      {
         EmbeddedContainer.stop();
      }

   }

   @Path("/curly")
   public static class CurlyBraces
   {
      @Path("{tableName:[a-z][a-z0-9_]{0,49}}")
      @GET
      @Produces("text/plain")
      public String get(@PathParam("tableName") String param)
      {
         return "param";
      }
   }

   /**
    * Test JIRA bug RESTEASY-227
    */
   @Test
   public void test227() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(CurlyBraces.class);
      try
      {
         ClientRequest request = new ClientRequest(generateURL("/curly/abcd"));
         ClientResponse<?> response = request.get();
         Assert.assertEquals(200, response.getStatus());
         response.releaseConnection();
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
}