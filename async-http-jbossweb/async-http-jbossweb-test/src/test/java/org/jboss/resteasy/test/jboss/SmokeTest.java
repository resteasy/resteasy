package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SmokeTest
{
   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         long start = System.currentTimeMillis();
         GetMethod method = new GetMethod("http://localhost:8080/resteasy/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         System.out.println("Time took: " + (System.currentTimeMillis() - start));
         method.releaseConnection();
      }
   }

}
