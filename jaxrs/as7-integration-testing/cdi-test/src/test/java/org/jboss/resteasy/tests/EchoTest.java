package org.jboss.resteasy.tests;

import org.jboss.resteasy.client.ClientRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class EchoTest
{
   @Test
   public void testIt2() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/cdi-test/jaxrs");
      String format = request.getTarget(String.class);
      System.out.println("Format: " + format);
      Assert.assertEquals("foo 1.1", format);
   }
}

