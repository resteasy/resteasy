package org.jboss.resteasy.test.asynch.resource;

public class AsyncInjectionContext implements AsyncInjectionContextInterface
{

   @Override
   public int foo()
   {
      return 42;
   }

}
