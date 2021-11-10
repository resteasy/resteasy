package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ValidationException;

public class ValidationExceptionOtherValidationException3 extends ValidationException {
   private static final long serialVersionUID = 1L;

   ValidationExceptionOtherValidationException3() {
   }

   ValidationExceptionOtherValidationException3(final Exception cause) {
      super(cause);
   }
}
