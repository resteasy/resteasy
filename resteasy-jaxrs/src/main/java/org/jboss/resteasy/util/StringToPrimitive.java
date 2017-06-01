package org.jboss.resteasy.util;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringToPrimitive
{
   public static Object stringToPrimitiveBoxType(Class primitiveType, String value)
   {
      if (primitiveType.equals(String.class)) return value;
      if (primitiveType.equals(boolean.class))
      {
         if (value == null) return Boolean.FALSE;
         return Boolean.valueOf(value);
      }
      else if (primitiveType.equals(char.class))
      {
    	  if(value==null)
    	  {
    		  return Character.valueOf(Character.MIN_VALUE);
    	  }
    	  else if (value.length() != 1) {
    		  throw new IllegalArgumentException();
    	  }
          return Character.valueOf(value.charAt(0));
       }
      if (value == null) value = "0";
      if (primitiveType.equals(int.class)) return Integer.valueOf(value);
      if (primitiveType.equals(long.class)) return Long.valueOf(value);
      if (primitiveType.equals(double.class)) return Double.valueOf(value);
      if (primitiveType.equals(float.class)) return Float.valueOf(value);
      if (primitiveType.equals(byte.class)) return Byte.valueOf(value);
      if (primitiveType.equals(short.class)) return Short.valueOf(value);
      if (primitiveType.equals(boolean.class)) return Boolean.valueOf(value);
      return null;

   }
}
