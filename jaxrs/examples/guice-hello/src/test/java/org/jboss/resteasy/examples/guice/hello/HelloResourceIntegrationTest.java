package org.jboss.resteasy.examples.guice.hello;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HelloResourceIntegrationTest
{
   @Test
   public void test() throws Exception
   {
      final URL url = new URL("http://localhost:9095/hello/world");
      final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      try {
         Assert.assertEquals("Hello world", reader.readLine());
         Assert.assertNull(reader.readLine());
      } finally {
         reader.close();
      }
   }
}
