package org.jboss.resteasy.tests;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tests.XFormat;
import org.jboss.resteasy.tests.XFormatProvider;
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

   @Test
   public void testHeaders() throws Exception
   {
      {
      ClientRequest request = new ClientRequest("http://localhost:8080/cdi-test/jaxrs/caching/conditional");
      String format = request.getTarget(String.class);
      System.out.println("Format: " + format);
      }
      {
      ClientRequest request = new ClientRequest("http://localhost:8080/cdi-test/jaxrs/caching/conditional");
      request.accept("application/xml")
              .header("If-Modified-Since", "Sun, 20 May 2012 17:08:26 GMT")
              .header("foo", "bar");
      String format = request.getTarget(String.class);
      System.out.println("Format: " + format);
      }
      {
      ClientRequest request = new ClientRequest("http://localhost:8080/cdi-test/jaxrs/caching/conditional");
      String format = request.getTarget(String.class);
      System.out.println("Format: " + format);
      }
      {
      ClientRequest request = new ClientRequest("http://localhost:8080/cdi-test/jaxrs/caching/conditional");
      request.accept("application/xml")
              .header("If-Modified-Since", "Sun, 20 May 2012 17:08:26 GMT")
              .header("foo", "bar");
      String format = request.getTarget(String.class);
      System.out.println("Format: " + format);
      }
   }
}

