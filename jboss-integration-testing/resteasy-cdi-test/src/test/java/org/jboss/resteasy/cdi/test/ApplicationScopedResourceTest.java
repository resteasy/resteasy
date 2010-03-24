package org.jboss.resteasy.cdi.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationScopedResourceTest extends AbstractResourceTest
{
   @Override
   protected String getTestPrefix()
   {
      return "applicationResource/";
   }
   
   @Test
   public void testTheSameInstanceIsUsedForEveryRequest()
   {
      HttpClient client = new HttpClient();
      GetMethod get1 = new GetMethod(BASE_URI + getTestPrefix());
      get1.addRequestHeader("Accept", "text/plain");
      GetMethod get2 = new GetMethod(BASE_URI + getTestPrefix());
      get2.addRequestHeader("Accept", "text/plain");
      try
      {
         int status1 = client.executeMethod(get1);
         assertEquals(status1, 200);
         String response1 = get1.getResponseBodyAsString();
         get1.releaseConnection();
         int status2 = client.executeMethod(get2);
         assertEquals(status2, 200);
         String response2 = get2.getResponseBodyAsString();
         get2.releaseConnection();
         assertEquals(response1, response2);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
