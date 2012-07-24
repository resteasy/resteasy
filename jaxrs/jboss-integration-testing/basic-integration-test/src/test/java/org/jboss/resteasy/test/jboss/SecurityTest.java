package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SecurityTest
{
   @Test
   public void testSecurity() throws Exception
   {
      HttpClient client = new HttpClient();
      
      client.getState().setCredentials(
          //new AuthScope(null, 8080, "Test"),
          new AuthScope(AuthScope.ANY),
          new UsernamePasswordCredentials("bill", "password")
      );
      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/security");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("Wild", method.getResponseBodyAsString());
         method.releaseConnection();
      }
   }

   @Test
   public void testSecurityFailure() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/security");
         int status = client.executeMethod(method);
         Assert.assertEquals(401, status);
         method.releaseConnection();
      }
   }

}
