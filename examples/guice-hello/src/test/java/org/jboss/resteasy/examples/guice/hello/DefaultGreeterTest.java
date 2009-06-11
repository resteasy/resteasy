package org.jboss.resteasy.examples.guice.hello;

import org.junit.Test;
import org.junit.Assert;

public class DefaultGreeterTest
{
   @Test
   public void testGreet()
   {
      final Greeter greeter = new DefaultGreeter();
      Assert.assertEquals("Hello foo", greeter.greet("foo"));
      Assert.assertEquals("Hello bar", greeter.greet("bar"));
   }
}
