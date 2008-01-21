package org.resteasy.util;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringToPrimitive
{
   public static Object stringToPrimitiveBoxType(Class primitiveType, String value)
   {
      if (primitiveType.equals(String.class)) return value;
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
