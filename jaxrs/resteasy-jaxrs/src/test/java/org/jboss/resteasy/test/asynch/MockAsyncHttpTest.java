package org.jboss.resteasy.test.asynch;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockAsyncHttpTest extends BaseResourceTest
{
   @Path("/")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      public void get(final @Suspend(1000) AsynchronousResponse response)
      {
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
                  response.setResponse(jaxrs);
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
   public void testMock() throws Exception
   {
      addPerRequestResource(MyResource.class);
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String response = get.getResponseBodyAsString();
      Assert.assertEquals(response, "hello");
   }
}
