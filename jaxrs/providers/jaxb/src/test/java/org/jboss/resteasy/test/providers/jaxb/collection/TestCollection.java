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
   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
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
}
