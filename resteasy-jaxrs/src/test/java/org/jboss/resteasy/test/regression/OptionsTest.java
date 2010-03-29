package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OptionsTest extends BaseResourceTest
{
   @Path("params")
   public static class ParamsResource
   {
      @Path("/customers/{custid}/phonenumbers")
      @GET
      @Produces("text/plain")
      public String getPhoneNumbers()
      {
         return "912-111-1111";
      }

      @Path("/customers/{custid}/phonenumbers/{id}")
      @GET
      @Produces("text/plain")
      public String getPhoneIds()
      {
         return "1111";
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      addPerRequestResource(ParamsResource.class);

   }

   @Test
   public void testOptions() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/params/customers/333/phonenumbers"));
      ClientResponse response = request.options();
      Assert.assertEquals(200, response.getStatus());

   }

   @Test
   public void testMethodNotAllowed() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/params/customers/333/phonenumbers"));
      ClientResponse response = request.post();
      Assert.assertEquals(405, response.getStatus());

   }
}
