package org.jboss.resteasy.examples.guice.hello;

public class DefaultGreeter implements Greeter
{
   public String greet(final String name)
   {
      return "Hello " + name;
   }
}
