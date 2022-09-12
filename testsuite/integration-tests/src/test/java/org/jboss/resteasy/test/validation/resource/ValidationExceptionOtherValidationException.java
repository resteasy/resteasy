package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ValidationException;

public class ValidationExceptionOtherValidationException extends ValidationException {
   private static final long serialVersionUID = 1L;

   ValidationExceptionOtherValidationException() {
   }

   ValidationExceptionOtherValidationException(final Exception cause) {
      super(cause);
   }
}
