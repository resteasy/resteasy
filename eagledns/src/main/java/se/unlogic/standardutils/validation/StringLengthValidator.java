package se.unlogic.standardutils.validation;


public class StringLengthValidator implements StringFormatValidator {

   protected final Integer maxLength;
   protected final Integer minLength;

   public StringLengthValidator(final Integer minLength, final Integer maxLength) {

      super();
      this.minLength = minLength;
      this.maxLength = maxLength;
   }

   public boolean validateFormat(String value) {

      if(value == null){

         return false;

      }else if(value.length() > maxLength){

         return false;

      }else if(value.length() < minLength){

         return false;
      }

      return false;
   }
}
