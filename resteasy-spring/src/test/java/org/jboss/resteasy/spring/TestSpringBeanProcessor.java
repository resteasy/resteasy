package org.jboss.resteasy.spring;

import static org.jboss.resteasy.test.TestPortProvider.createGetMethod;
import static org.jboss.resteasy.test.TestPortProvider.createPostMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import junit.framework.Assert;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-bean-processor-test.xml" })
public class TestSpringBeanProcessor 
{
   public static class Customer
   {
      private String name;

      public Customer()
      {
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Provider
   @Produces("foo/bar")
   public static class MyWriter implements MessageBodyWriter<Customer>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Customer.class.isAssignableFrom(type);
      }

      public long getSize(Customer customer, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType)
      {
         return customer.getName().getBytes().length;
      }

      public void writeTo(Customer customer, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException
      {
         entityStream.write(customer.getName().getBytes());
      }
   }

   @Path("/")
   public static class MyResource
   {
      private Customer customer;

      public MyResource()
      {
      }

      @GET
      @Produces("foo/bar")
      public Customer callGet()
      {
         return customer;
      }

      public Customer getCustomer()
      {
         return customer;
      }

      public void setCustomer(Customer customer)
      {
         this.customer = customer;
      }
   }

   @Path("/prototyped")
   public static class MyPrototypedResource
   {
      private Customer customer;

      private int counter = 0;

      @PathParam("id")
      private String id;

      public MyPrototypedResource()
      {
         System.out.println("here");
      }

      @GET
      @Path("{id}")
      @Produces("text/plain")
      public String callGet()
      {
         Assert.assertEquals(id, "1");
         return customer.getName() + (counter++);
      }

      public Customer getCustomer()
      {
         return customer;
      }

      public void setCustomer(Customer customer)
      {
         this.customer = customer;
      }

   }

   @Path("/intercepted")
   public static class MyInterceptedResource implements MyIntercepted
   {
      private Customer customer;

      @GET
      @Produces("foo/bar")
      public Customer callGet()
      {
         return customer;
      }

      public Customer getCustomer()
      {
         return customer;
      }

      public void setCustomer(Customer customer)
      {
         this.customer = customer;
      }
   }

   @Path("/count")
   public static class Counter
   {
      int counter;

      @POST
      public String count()
      {
         return Integer.toString(counter++);
      }
   }

   public static class MyInterceptor implements MethodInterceptor
   {
      public static boolean invoked = false;

      public Object invoke(MethodInvocation methodInvocation) throws Throwable
      {
         System.out.println("HERE!!!!");
         invoked = true;
         return methodInvocation.proceed();
      }
   }

   @Test
   public void testAutoProxy() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/intercepted");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      Assert.assertEquals(get.getResponseBodyAsString(), "bill");
      Assert.assertTrue(MyInterceptor.invoked);
   }

   @Test
   public void testProcessor() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      Assert.assertEquals(get.getResponseBodyAsString(), "bill");
   }

   @Test
   public void testPrototyped() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/prototyped/1");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      Assert.assertEquals(get.getResponseBodyAsString(), "bill0");
      status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      Assert.assertEquals(get.getResponseBodyAsString(), "bill0");
   }

   @Test
   public void testRegistration() throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod post = createPostMethod("/registered/singleton/count");
      int status = client.executeMethod(post);
      Assert.assertEquals(200, status);
      Assert.assertEquals(post.getResponseBodyAsString(), "0");

      post = createPostMethod("/count");
      status = client.executeMethod(post);
      Assert.assertEquals(404, status);
   }

   @Test
   public void testScanned() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/scanned");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      Assert.assertEquals(get.getResponseBodyAsString(), "Hello");

   }
}
