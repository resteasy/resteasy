package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TimeoutTest extends BaseResourceTest
{
   /**
    * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
    * @version $Revision: 1 $
    */
   @Path("/timeout")
   public static interface TimeoutResource
   {
      @GET
      @Produces("text/plain")
      String get(@QueryParam("sleep") int sleep)  throws Exception;
   }

   @Path("/timeout")
   public static class TimeoutResourceBean implements TimeoutResource
   {
      @Override
      @GET
      @Produces("text/plain")
      public String get(@QueryParam("sleep") int sleep)  throws Exception
      {
          Thread.sleep(sleep * 1000);
          return "OK";
      }
   }

   @BeforeClass
   public static void reg()
   {
      addPerRequestResource(TimeoutResourceBean.class);
   }

   @Test
   public void testEcho()  throws Exception
   {
      ResteasyClient client1 = new ResteasyClientBuilder().socketTimeout(2, TimeUnit.SECONDS).build();
      ClientHttpEngine engine = client1.httpEngine();
      Assert.assertNotNull(engine);

      ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
      ResteasyWebTarget target = client.target(TestPortProvider.generateURL("/timeout"));
      try
      {
         Response response = target.queryParam("sleep", "5").request().get();
         Assert.fail();
      }
      catch (ProcessingException e)
      {
         Assert.assertEquals(e.getCause().getClass(), SocketTimeoutException.class);
      }

      TimeoutResource proxy = client.target(TestPortProvider.generateURL("")).proxy(TimeoutResource.class);
      try
      {
         proxy.get(5);
         Assert.fail();
      }
      catch (ProcessingException e)
      {
         Assert.assertEquals(e.getCause().getClass(), SocketTimeoutException.class);
      }
      client1.close();

   }
}
