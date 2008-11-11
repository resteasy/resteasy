package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

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

      @Path("/complex")
      @GET
      public Object getComplex()
      {
         Response.ResponseBuilder builder = Response.status(HttpResponseCodes.SC_FOUND).entity("hello world".getBytes());
         builder.header("CoNtEnT-type", "text/plain");
         return builder.build();
      }

      @Path("/implicit")
      @GET
      @Produces("application/xml")
      public Response getCustomer()
      {
         System.out.println("GET CUSTOEMR");
         Customer cust = new Customer();
         cust.setName("bill");
         return Response.ok(cust).build();
      }

   }

   @Provider
   @Produces("application/xml")
   public static class CustomerWriter implements MessageBodyWriter<Customer>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Customer.class);
      }

      public long getSize(Customer customer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      public void writeTo(Customer customer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
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
      dispatcher = EmbeddedContainer.start();
      dispatcher.getProviderFactory().addMessageBodyWriter(CustomerWriter.class);
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/implicit");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "application/xml");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("<customer><name>bill</name></customer>", response);
         method.releaseConnection();
      }
      EmbeddedContainer.stop();
   }

   /**
    * Test JIRA bugs RESTEASY-1 and RESTEASY-2
    *
    * @throws Exception
    */
   @Test
   public void test1and2() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/simple");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "text/plain");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", response);
         method.releaseConnection();
      }
      EmbeddedContainer.stop();
   }

   /**
    * Test JIRA bugs RESTEASY-61
    *
    * @throws Exception
    */
   @Test
   public void testJdkURLConnection() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         URL url = new URL("http://localhost:8081/simple");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         @SuppressWarnings("unused")
         Object obj = conn.getContent();
      }
      EmbeddedContainer.stop();
   }


   /**
    * Test JIRA bug RESTEASY-24 and 139
    */
   @Test
   public void test24() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/complex");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_FOUND, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "text/plain");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", response);
         method.releaseConnection();
      }
      EmbeddedContainer.stop();

   }


}