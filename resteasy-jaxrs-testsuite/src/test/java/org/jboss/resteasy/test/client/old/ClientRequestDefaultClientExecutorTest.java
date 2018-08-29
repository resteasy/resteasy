package org.jboss.resteasy.test.client.old;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Unit test for https://issues.jboss.org/browse/RESTEASY-623.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class ClientRequestDefaultClientExecutorTest extends BaseResourceTest
{

   private static final Logger LOG = Logger.getLogger(ClientRequestDefaultClientExecutorTest.class);

   @Path("/test")
   public interface TestService
   {  
      @POST
      ClientResponse<String> post();
   }
   
   @Path("/test")
   public static class TestServiceImpl
   {
      @POST
      public void post()
      {
         LOG.info("In POST");
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestServiceImpl.class);
   }

   /**
    * Verify that each ClientRequest.setDefaultExecutorClass()
    * still works.
    */
   @Test
   public void testClientRequestNonSharedExecutor() throws Exception
   {
      ClientRequest.setDefaultExecutorClass(TestClientExecutor.class.getName());
      ClientRequest request = new ClientRequest(generateURL("/test"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      Assert.assertTrue(request.getExecutor() instanceof TestClientExecutor);
      response.releaseConnection();
   }
   
   public static class TestClientExecutor extends ApacheHttpClient4Executor
   {
   }
}
