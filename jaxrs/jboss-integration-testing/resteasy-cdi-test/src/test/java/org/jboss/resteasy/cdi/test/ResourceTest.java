package org.jboss.resteasy.cdi.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ResourceTest extends AbstractResourceTest
{
   protected String getTestPrefix()
   {
      return "resource/";
   }
   
   @Test
   public void testJaxrsFieldInjection2()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "jaxrsFieldInjection2?foo=bar", "bar");
   }

   @Test
   public void testNewInstanceCreatedForEveryRequest()
   {
      HttpClient client = new HttpClient();
      GetMethod get1 = new GetMethod(BASE_URI + getTestPrefix() + "toString");
      get1.addRequestHeader("Accept", "text/plain");
      GetMethod get2 = new GetMethod(BASE_URI + getTestPrefix() + "toString");
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
         assertFalse(response1.equals(response2));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
