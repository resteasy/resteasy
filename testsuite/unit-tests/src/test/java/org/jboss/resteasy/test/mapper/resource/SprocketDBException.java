package org.jboss.resteasy.test.mapper.resource;

public class SprocketDBException extends RuntimeException {

   public SprocketDBException(final String s, final Throwable throwable) {
      super(s, throwable);
   }

   public SprocketDBException(final String s) {
      super(s);
   }

   public SprocketDBException(final Throwable throwable) {
      super(throwable);
   }

   public SprocketDBException() {
      super();
   }
}
// SprocketDBExceptionMapper
