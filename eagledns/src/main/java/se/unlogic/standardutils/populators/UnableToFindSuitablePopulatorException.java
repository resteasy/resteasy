package se.unlogic.standardutils.populators;


public class UnableToFindSuitablePopulatorException extends Exception {

   private static final long serialVersionUID = -214177380194928711L;

   public UnableToFindSuitablePopulatorException(final String message, final Throwable cause) {

      super(message, cause);
   }

   public UnableToFindSuitablePopulatorException(final String message) {

      super(message);
   }

   public UnableToFindSuitablePopulatorException(final Throwable cause) {

      super(cause);
   }

}
