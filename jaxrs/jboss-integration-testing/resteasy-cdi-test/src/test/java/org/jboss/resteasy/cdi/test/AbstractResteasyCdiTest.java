package org.jboss.resteasy.cdi.test;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractResteasyCdiTest
{
   private HttpClient client = new HttpClient();
   public static final String BASE_URI = "http://localhost:8080/resteasy-cdi/";
   
   public void testPlainTextReadonlyResource(String uri, String body)
   {
      GetMethod get = new GetMethod(uri);
      get.addRequestHeader("Accept", "text/plain");
      try
      {
         int status = client.executeMethod(get);
         assertEquals(status, 200);
         assertTrue(get.getResponseBodyAsString().contains(body));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         get.releaseConnection();
      }
   }
   
   public void testPlainTextReadonlyResource(String uri, boolean body)
   {
      testPlainTextReadonlyResource(uri, String.valueOf(body));
   }
}
