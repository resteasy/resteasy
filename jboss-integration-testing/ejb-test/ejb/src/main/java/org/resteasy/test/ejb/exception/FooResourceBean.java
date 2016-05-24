package org.resteasy.test.ejb.exception;

import javax.ejb.Stateless;

@Stateless
public class FooResourceBean implements FooResource
{
   public void testException()
   {
      throw new FooException();
   }
}
