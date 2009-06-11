package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QualityFactorTest extends BaseResourceTest
{
   @XmlRootElement
   public static class Thing
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

   @Path("/test")
   public static class TestService
   {
      @GET
      @Produces({"application/json", "application/xml"})
      public Thing get(@HeaderParam("Accept") String accept)
      {
         System.out.println(accept);
         Thing thing = new Thing();
         thing.setName("Bill");
         return thing;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestService.class);
   }

   @Test
   public void testHeader() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.accept("application/xml; q=0.5");
      request.accept("application/json; q=0.8");
      String response = request.getTarget(String.class);
      System.out.println(response);
      Assert.assertTrue(response.startsWith("{"));

   }
}
