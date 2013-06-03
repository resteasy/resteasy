package org.jboss.resteasy.spi.validation;

import java.lang.reflect.Method;
import java.util.HashSet;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;

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
   
   static public boolean isValidatible(Method m)
   {
      ExecutableType[] types = null;
      ValidateOnExecution voe = m.getAnnotation(ValidateOnExecution.class);
      if (voe == null || voe.type().length == 0)
      {
         types = getSuperMethodExecutableTypes(m);
         if (types == null)
         {
            types = getClassExecutableTypes(m);
         }
      }
      else
      {
         types = voe.type();
      }
      if (types == null || types.length == 0)
      {
         return true;
      }
      boolean isGetterMethod = isGetter(m);
      for (int i = 0; i < types.length; i++)
      {
         switch (types[i])
         {
            case IMPLICIT:
            case ALL:
               return true;
               
            case NONE:
               continue;
               
            case NON_GETTER_METHODS:
               if (!isGetterMethod)
               {
                  return true;
               }
               continue;
               
            case GETTER_METHODS:
               if (isGetterMethod)
               {
                  return true;
               }
               continue;
               
            default: 
               continue;
         }
      }
      return false;
   }
   
   static protected ExecutableType[] getSuperMethodExecutableTypes(Method m)
   {
      Class<?> c = m.getDeclaringClass().getSuperclass();
      ExecutableType[] executableTypes = null;
      while (c != null)
      {
         try
         {
            Method superMethod = c.getDeclaredMethod(m.getName(), m.getParameterTypes());
            if (superMethod == null)
            {
               continue;
            }
            ValidateOnExecution voe = superMethod.getAnnotation(ValidateOnExecution.class);
            if (voe == null || voe.type().length == 0)
            {
               continue;
            }
            ExecutableType[] types = voe.type();
            if (types == null || types.length == 0)
            {
               continue;
            }
            executableTypes = types;
         }
         catch (NoSuchMethodException e)
         {
            // Ignore.
         }
         finally
         {
            c = c.getSuperclass();
         }
      }
      return executableTypes;
   }
   
   static protected ExecutableType[] getClassExecutableTypes(Method m)
   {
      ValidateOnExecution voe = m.getDeclaringClass().getAnnotation(ValidateOnExecution.class);
      if (voe == null)
      {
         return null;
      }
      ExecutableType[] types = voe.type();
      if (types == null || types.length == 0)
      {
         return null;
      }
      for (int i = 0; i < types.length; i++)
      {
         if (types[i].equals(ExecutableType.IMPLICIT))
         {
            return null;
         }
      }
      return types;
   }
   
   static protected boolean isGetter(Method m)
   {
      String name = m.getName();
      Class<?> returnType = m.getReturnType();
      if (returnType.equals(Void.class))
      {
         return false;
      }
      if (m.getParameterTypes().length > 0)
      {
         return false;
      }
      if (name.startsWith("get"))
      {
         return true;
      }
      if (name.startsWith("is") && returnType.equals(Boolean.class))
      {
         return true;
      }
      return false;
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
