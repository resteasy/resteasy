package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class EjbExceptionUnwrapLocatingResourceBean implements EjbExceptionUnwrapLocatingResource {
   @EJB
   EjbExceptionUnwrapSimpleResource simple;

   public EjbExceptionUnwrapSimpleResource getLocating() {
      return simple;
   }
}
