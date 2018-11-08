package se.unlogic.standardutils.reflection;


public class MethodNotFoundException extends Exception {

   private static final long serialVersionUID = -8580739778092215878L;

   public MethodNotFoundException() {

      super();
   }

   public MethodNotFoundException(final String message, final Throwable cause) {

      super(message, cause);
   }

   public MethodNotFoundException(final String message) {

      super(message);
   }

   public MethodNotFoundException(final Throwable cause) {

      super(cause);
   }
}
