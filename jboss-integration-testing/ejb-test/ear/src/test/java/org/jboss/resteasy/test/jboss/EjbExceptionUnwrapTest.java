package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Test;

public class EjbExceptionUnwrapTest
{
   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpClient client = new HttpClient();

      GetMethod method = new GetMethod("http://localhost:8080/test-war/exception");
      int status = client.executeMethod(method);
      Assert.assertEquals(409, status);
      method.releaseConnection();
   }
}
