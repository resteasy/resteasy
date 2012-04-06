package org.jboss.resteasy.examples.asyncjob;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncJobTest
{
	   
   @Test
   public void testOneway() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/resource?oneway=true");
      request.body("text/plain", "content");
      ClientResponse<String> response = request.put(String.class);
      Assert.assertEquals(202, response.getStatus());
      response.releaseConnection();
      Thread.sleep(1500);
      request = new ClientRequest("http://localhost:9095/resource");
      response = request.get(String.class);
      Assert.assertEquals(Integer.toString(1), response.getEntity());
   }

   @Test
   public void testAsynch() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/resource?asynch=true");
      request.body("text/plain", "content");
      ClientResponse<String> response = request.post(String.class);
      Assert.assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
      String jobUrl1 = response.getHeaders().getFirst(HttpHeaders.LOCATION);
      System.out.println("jobUrl1: " + jobUrl1);
      response.releaseConnection();
      
      request = new ClientRequest(jobUrl1);
      response = request.get(String.class);
      Assert.assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
      response.releaseConnection();
      
      Thread.sleep(1500);
      response = request.get(String.class);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertEquals("content", response.getEntity());
   }
}
