package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.HttpURLConnection;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * RESTEASY-306
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseTest extends BaseResourceTest
{

   @Path("/user")
   @Produces("application/xml")
   public interface RESTCredits
   {
      @Path("/{userId}/inventory/credits")
      @GET
      public Response getCredits(@PathParam("userId") String userId);
   }

   @XmlRootElement
   public static class Credit
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

   public static class CreditService implements RESTCredits
   {
      public Response getCredits(@PathParam("userId") String userId)
      {
         Credit credit = new Credit();
         credit.setName("foobar");
         return Response.ok(credit).build();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(CreditService.class);
   }

   @Test
   public void testIt() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      RESTCredits proxy = ProxyFactory.create(RESTCredits.class, generateBaseUrl());
      ClientResponse<?> response = (ClientResponse<?>) proxy.getCredits("xx");
      Assert.assertEquals(response.getStatus(), HttpURLConnection.HTTP_OK);
      Credit cred = response.getEntity(Credit.class);
   }


}
