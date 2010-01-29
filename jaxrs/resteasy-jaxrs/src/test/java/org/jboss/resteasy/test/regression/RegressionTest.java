package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
      {
         HttpClient client = new HttpClient();
         GetMethod method = createGetMethod("/implicit");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "application/xml");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("<customer><name>bill</name></customer>", response);

         DeleteMethod del = createDeleteMethod("/implicit");
         status = client.executeMethod(del);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);

         SimpleClient proxy = ProxyFactory.create(SimpleClient.class, generateBaseUrl());
         proxy.deleteCustomer();

         Assert.assertEquals(204, proxy.deleteComplex().getStatus());

         method.releaseConnection();
         client.getHttpConnectionManager().closeIdleConnections(0);
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
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = createGetMethod("/simple");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "text/plain");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", response);
         method.releaseConnection();
         client.getHttpConnectionManager().closeIdleConnections(0);
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
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         URL url = createURL("/simple");
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
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = createGetMethod("/complex");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_FOUND, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "text/plain");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", response);
         method.releaseConnection();
         client.getHttpConnectionManager().closeIdleConnections(0);
      }
      EmbeddedContainer.stop();

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
      {
         HttpClient client = new HttpClient();
         GetMethod method = createGetMethod("/spaces/with%20spaces/without");
         int status = client.executeMethod(method);
         Assert.assertEquals(200, status);
         method.releaseConnection();
         client.getHttpConnectionManager().closeIdleConnections(0);
      }
      EmbeddedContainer.stop();

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
      {
         HttpClient client = new HttpClient();
         GetMethod method = createGetMethod("/curly/abcd");
         int status = client.executeMethod(method);
         Assert.assertEquals(200, status);
         method.releaseConnection();
         client.getHttpConnectionManager().closeIdleConnections(0);
      }
      EmbeddedContainer.stop();

   }
}