package org.jboss.resteasy.test.link;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkTest  extends BaseResourceTest
{
   @Path("/")
   public static class CustomerResource
   {
      @GET
      @Produces("application/xml")
      public Customer getCustomer() {
         Customer cust = new Customer("bill");
         Link link = Link.fromUri("a/b/c").build();
         cust.getLinks().add(link);
         link = Link.fromUri("c/d").rel("delete").build();
         cust.getLinks().add(link);
         return cust;
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(CustomerResource.class);
   }

   @Test
   public void testCustomer() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String str = client.target(generateURL("")).request().get(String.class);
      System.out.println(str);
   }
}
