package org.jboss.resteasy.test.nextgen.asynch;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncTest extends BaseResourceTest
{
   @Path("/")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      public void get(@Suspended final AsyncResponse response)
      {
         response.setTimeout(2000, TimeUnit.MILLISECONDS);
         Thread t = new Thread()
         {
            @Override
            public void run()
            {
               try
               {
                  System.out.println("STARTED!!!!");
                  Thread.sleep(100);
                  Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
                  response.resume(jaxrs);
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
         };
         t.start();
      }

      @GET
      @Path("timeout")
      @Produces("text/plain")
      public void timeout(@Suspended final AsyncResponse response)
      {
         response.setTimeout(100, TimeUnit.MILLISECONDS);
         Thread t = new Thread()
         {
            @Override
            public void run()
            {
               try
               {
                  System.out.println("STARTED!!!!");
                  Thread.sleep(1000);
                  Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
                  response.resume(jaxrs);
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
         };
         t.start();
      }
   }

   @Test
   public void testSuccess() throws Exception
   {
      addPerRequestResource(MyResource.class);
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.readEntity(String.class));
      response.close();
      client.close();
   }

   @Test
   public void testTimeout() throws Exception
   {
      addPerRequestResource(MyResource.class);

      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/timeout")).request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
      client.close();
   }

}
