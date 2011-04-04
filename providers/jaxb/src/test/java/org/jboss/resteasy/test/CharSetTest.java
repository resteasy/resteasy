package org.jboss.resteasy.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CharSetTest extends BaseResourceTest
{

   @XmlRootElement(name="customer")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   public static class Customer
   {
      private String name;

      @XmlElement
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Path("/test")
   public static class TestService
   {
      @POST
      @Consumes("application/xml")
      public void post(Customer cust)
      {
         System.out.println(cust.getName());
      }

      @POST
      @Path("string")
      public void postString(String cust)
      {
         System.out.println("*******");
         System.out.println(cust);
      }
   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(TestService.class);
   }

   @Test
   public void testCase2() throws Exception
   {

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test/string"));
      Customer cust = new Customer();
      String name = "bill\u00E9";
      cust.setName(name);
      request.body("application/xml", cust);
      request.post();


   }
   @Test
   public void testCase() throws Exception
   {

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test"));
      Customer cust = new Customer();
      String name = "bill\u00E9";
      System.out.println("client name: " + name);

      System.out.println("bytes string: " + new String(name.getBytes("UTF-8"), "UTF-8"));
      cust.setName(name);
      request.body("application/xml", cust);
      request.post();


   }
}
