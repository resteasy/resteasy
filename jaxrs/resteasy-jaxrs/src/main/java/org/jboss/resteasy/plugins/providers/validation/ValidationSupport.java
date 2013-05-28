package org.jboss.resteasy.plugins.providers.validation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jboss.resteasy.validation.ResteasyConstraintViolation;



/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 11, 2013
 */
public class ValidationSupport
{
   public static final String VALIDATION_HEADER = "validation-exception";
   
   private static ValidatorFactory factory;
   
   static public Validator getValidator()
   {
      if (factory == null)
      {
         factory = Validation.buildDefaultValidatorFactory();
      }
      return factory.getValidator();
   }
   
   static public void closeValidatorFactory()
   {
      if (factory != null)
      {
         factory.close();
         factory = null;
      }
   }
   
//   static class FieldViolation extends ResteasyConstraintViolation
//   {
//      private static final long serialVersionUID = -8728585145967016578L;
//      
//      public FieldViolation(String path, String message, String value)
//      {
//         super(path, message, value);
//      }
//      public String type()
//      {
//         return "field";
//      }
//   }
//   
//   static class PropertyViolation extends ResteasyConstraintViolation
//   {
//      private static final long serialVersionUID = -1035403187335143272L;
//      
//      public PropertyViolation(String path, String message, String value)
//      {
//         super(path, message, value);
//      }
//      public String type()
//      {
//         return "property";
//      }
//   }
//   
//   static class ClassViolation extends ResteasyConstraintViolation
//   {
//      private static final long serialVersionUID = -8720733237574807640L;
//      
//      public ClassViolation(String path, String message, String value)
//      {
//         super(path, message, value);
//      }
//      public String type()
//      {
//         return "class";
//      }
//   }
//   
//   static class ParameterViolation extends ResteasyConstraintViolation
//   {
//      private static final long serialVersionUID = -8502080979598636564L;
//      
//      public ParameterViolation(String path, String message, String value)
//      {
//         super(path, message, value);
//      }
//      public String type()
//      {
//         return "parameter";
//      }
//   }
//   
//   static class ReturnValueViolation extends ResteasyConstraintViolation
//   {
//      private static final long serialVersionUID = -5378753756866647837L;
//      
//      public ReturnValueViolation(String path, String message, String value)
//      {
//         super(path, message, value);
//      }
//      public String type()
//      {
//         return "return value";
//      }
//   }
   
   static public String getViolationType(ResteasyConstraintViolation v)
   {
      return v.type();
   }
   
   static public String getViolationTarget(ResteasyConstraintViolation v)
   {
     return v.getPath();
   }
   
   static public String getViolationMessage(ResteasyConstraintViolation v)
   {
      return v.getMessage();    
   }
   
   static public String getViolationValue(ResteasyConstraintViolation v)
   {
      return v.getValue();
   }   
}
