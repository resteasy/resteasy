package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ValidationException;

public class ValidationExceptionOtherValidationException2 extends ValidationException {
   private static final long serialVersionUID = 1L;

   ValidationExceptionOtherValidationException2() {
   }

   ValidationExceptionOtherValidationException2(final Exception cause) {
      super(cause);
   }
}
