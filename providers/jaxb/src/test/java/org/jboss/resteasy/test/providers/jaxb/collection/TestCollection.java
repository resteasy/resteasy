package org.jboss.resteasy.test.providers.jaxb.collection;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestCollection extends BaseResourceTest
{
   @Path("/")
   public static class MyResource
   {
      @GET
      @Path("array")
      @Produces("application/xml")
      @Wrapped
      public Customer[] getCustomers()
      {
         Customer[] custs = {new Customer("bill"), new Customer("monica")};
         return custs;
      }

      @PUT
      @Path("array")
      @Consumes("application/xml")
      public void putCustomers(@Wrapped Customer[] customers)
      {
         Assert.assertEquals("bill", customers[0].getName());
         Assert.assertEquals("monica", customers[1].getName());
      }

      @GET
      @Path("set")
      @Produces("application/xml")
      @Wrapped
      public Set<Customer> getCustomerSet()
      {
         HashSet<Customer> set = new HashSet<Customer>();
         set.add(new Customer("bill"));
         set.add(new Customer("monica"));

         return set;
      }

      @PUT
      @Path("list")
      @Consumes("application/xml")
      public void putCustomers(@Wrapped List<Customer> customers)
      {
         Assert.assertEquals("bill", customers.get(0).getName());
         Assert.assertEquals("monica", customers.get(1).getName());
      }

      @GET
      @Path("list")
      @Produces("application/xml")
      @Wrapped
      public List<Customer> getCustomerList()
      {
         ArrayList<Customer> set = new ArrayList<Customer>();
         set.add(new Customer("bill"));
         set.add(new Customer("monica"));

         return set;
      }

      @GET
      @Path("list/response")
      @Produces("application/xml")
      @Wrapped
      public Response getCustomerListResponse()
      {
         ArrayList<Customer> set = new ArrayList<Customer>();
         set.add(new Customer("bill"));
         set.add(new Customer("monica"));
         GenericEntity<List<Customer>> genericEntity = new GenericEntity<List<Customer>>(set)
         {
         };
         return Response.ok(genericEntity).build();
      }
   }

   @Path("/namespaced")
   public static class MyNamespacedResource
   {
      @GET
      @Path("array")
      @Produces("application/xml")
      @Wrapped
      public NamespacedCustomer[] getCustomers()
      {
         NamespacedCustomer[] custs = {new NamespacedCustomer("bill"), new NamespacedCustomer("monica")};
         return custs;
      }

      @PUT
      @Path("array")
      @Consumes("application/xml")
      public void putCustomers(@Wrapped NamespacedCustomer[] customers)
      {
         Assert.assertEquals("bill", customers[0].getName());
         Assert.assertEquals("monica", customers[1].getName());
      }

      @GET
      @Path("set")
      @Produces("application/xml")
      @Wrapped
      public Set<NamespacedCustomer> getCustomerSet()
      {
         HashSet<NamespacedCustomer> set = new HashSet<NamespacedCustomer>();
         set.add(new NamespacedCustomer("bill"));
         set.add(new NamespacedCustomer("monica"));

         return set;
      }

      @PUT
      @Path("list")
      @Consumes("application/xml")
      public void putCustomers(@Wrapped List<NamespacedCustomer> customers)
      {
         Assert.assertEquals("bill", customers.get(0).getName());
         Assert.assertEquals("monica", customers.get(1).getName());
      }

      @GET
      @Path("list")
      @Produces("application/xml")
      @Wrapped
      public List<NamespacedCustomer> getCustomerList()
      {
         ArrayList<NamespacedCustomer> set = new ArrayList<NamespacedCustomer>();
         set.add(new NamespacedCustomer("bill"));
         set.add(new NamespacedCustomer("monica"));

         return set;
      }

      @GET
      @Path("list/response")
      @Produces("application/xml")
      @Wrapped
      public Response getCustomerListResponse()
      {
         ArrayList<NamespacedCustomer> set = new ArrayList<NamespacedCustomer>();
         set.add(new NamespacedCustomer("bill"));
         set.add(new NamespacedCustomer("monica"));
         GenericEntity<List<NamespacedCustomer>> genericEntity = new GenericEntity<List<NamespacedCustomer>>(set)
         {
         };
         return Response.ok(genericEntity).build();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
      dispatcher.getRegistry().addPerRequestResource(MyNamespacedResource.class);
   }

   @Test
   public void testArray() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/array");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String str = get.getResponseBodyAsString();
      System.out.println(str);
      PutMethod put = createPutMethod("/array");
      put.setRequestEntity(new StringRequestEntity(str, "application/xml", null));
      status = client.executeMethod(put);
      Assert.assertEquals(204, status);

   }

   @Test
   public void testList() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/list");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String str = get.getResponseBodyAsString();
      System.out.println(str);
      PutMethod put = createPutMethod("/list");
      put.setRequestEntity(new StringRequestEntity(str, "application/xml", null));
      status = client.executeMethod(put);
      Assert.assertEquals(204, status);

   }

   @Test
   public void testResponse() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/list/response");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String str = get.getResponseBodyAsString();
      System.out.println(str);
   }

   @Test
   public void testNamespacedArray() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/namespaced/array");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String str = get.getResponseBodyAsString();
      System.out.println(str);
      PutMethod put = createPutMethod("/namespaced/array");
      put.setRequestEntity(new StringRequestEntity(str, "application/xml", null));
      status = client.executeMethod(put);
      Assert.assertEquals(204, status);

   }

   @Test
   public void testNamespacedList() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/namespaced/list");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String str = get.getResponseBodyAsString();
      System.out.println(str);
      PutMethod put = createPutMethod("/namespaced/list");
      put.setRequestEntity(new StringRequestEntity(str, "application/xml", null));
      status = client.executeMethod(put);
      Assert.assertEquals(204, status);

   }

   @Test
   public void testNamespacedResponse() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/namespaced/list/response");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String str = get.getResponseBodyAsString();
      System.out.println(str);
   }

}
