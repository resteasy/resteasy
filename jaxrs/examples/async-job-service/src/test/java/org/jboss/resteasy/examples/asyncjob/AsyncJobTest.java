package org.jboss.resteasy.examples.asyncjob;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncJobTest
{
   @Test
   public void testOneway() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         PutMethod method = new PutMethod("http://localhost:9095/resource?oneway=true");
         method.setRequestEntity(new StringRequestEntity("content", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(202, status);
         Thread.sleep(1500);
         GetMethod get = new GetMethod("http://localhost:9095/resource");
         status = client.executeMethod(get);
         Assert.assertEquals(Integer.toString(1), get.getResponseBodyAsString());

         method.releaseConnection();
      }
   }

   @Test
   public void testAsynch() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         PostMethod method = new PostMethod("http://localhost:9095/resource?asynch=true");
         method.setRequestEntity(new StringRequestEntity("content", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(Response.Status.ACCEPTED.getStatusCode(), status);
         String jobUrl1 = method.getResponseHeader(HttpHeaders.LOCATION).getValue();

         GetMethod get = new GetMethod(jobUrl1);
         status = client.executeMethod(get);
         Assert.assertEquals(Response.Status.ACCEPTED.getStatusCode(), status);

         Thread.sleep(1500);
         status = client.executeMethod(get);
         Assert.assertEquals(Response.Status.OK.getStatusCode(), status);
         Assert.assertEquals(get.getResponseBodyAsString(), "content");

         method.releaseConnection();
      }
   }
}
