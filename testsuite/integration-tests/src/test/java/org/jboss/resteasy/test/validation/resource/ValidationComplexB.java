package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.Valid;

public class ValidationComplexB {
   @Valid
   ValidationComplexA a;

   public ValidationComplexB(final ValidationComplexA a) {
      this.a = a;
   }
}
