package org.jboss.resteasy.examples.guice.hello;

import org.junit.Test;
import org.junit.Assert;

public class HelloResourceTest
{
   @Test
   public void testHello() {
      final HelloResource helloResource = new HelloResource(new Greeter()
      {
         public String greet(final String name)
         {
            return "greeting";
         }
      });
      Assert.assertEquals("greeting", helloResource.hello("foo"));
   }
}
