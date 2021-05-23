package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.Stateless;

@Stateless
public class EjbExceptionUnwrapFooResourceBean implements EjbExceptionUnwrapFooResource {
   public void testException() {
      throw new EjbExceptionUnwrapFooException();
   }
}
