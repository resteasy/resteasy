/**
 * 
 */
package org.jboss.resteasy.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.resteasy.core.LoggerCategories;
import org.slf4j.Logger;

/**
 * 
 * A utility class uses to return a String value as a typed object. 
 * 
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 */
public final class TypeConverter
{
   private static final Logger logger = LoggerCategories.getCoreLogger();

   /**
    * A map of primitive to objects.
    */
   private static final Map<Class<?>, Class<?>> PRIMITIVES;

   static {
      PRIMITIVES = new HashMap<Class<?>, Class<?>>();
      PRIMITIVES.put(int.class, Integer.class);
      PRIMITIVES.put(double.class, Double.class);
      PRIMITIVES.put(float.class, Float.class);
      PRIMITIVES.put(short.class, Short.class);
      PRIMITIVES.put(byte.class, Byte.class);
      PRIMITIVES.put(long.class, Long.class);
   }

   private TypeConverter()
   {

   }

   /**
    * A generic method that returns the {@link String} as the specified Java type. 
    * @param <T> the type to return
    * @param source the string value to convert
    * @param targetType 
    * @return the object instance
    */
   public static <T> T getType(final Class<T> targetType, final String source)
   {
      /*
       * Dates are too complicated for this class.
       */
      if (Date.class.isAssignableFrom(targetType))
      {
         throw new IllegalArgumentException("Date instances are not supported by this class.");
      }
      T result;
      if (Boolean.class.equals(targetType) || boolean.class.equals(targetType))
      {
         return targetType.cast(getBooleanValue(source));
      }
      try
      {
         result = getTypeViaValueOfMethod(source, targetType);
      }
      catch (NoSuchMethodException e)
      {
         logger.warn("No valueOf() method available for {}, trying constructor...", targetType
               .getSimpleName());
         result = getTypeViaStringConstructor(source, targetType);
      }
      return result;
   }

   /**
    * <p>
    * Returns a Boolean value from a String. Unlike {@link Boolean.#valueOf(String)}, this
    * method takes more String options. The following String values will return true:
    * </p>
    * <ul>
    *   <li>Yes</li>
    *   <li>Y</li>
    *   <li>T</li>
    *   <li>1</li>
    * </ul>
    * <p>
    * While the following values will return false:
    * </p>
    * <ul>
    *   <li>No</li>
    *   <li>N</li>
    *   <li>F</li>
    *   <li>0</li>
    * </ul>
    * 
    * @param source
    * @return
    */
   public static Boolean getBooleanValue(final String source)
   {
      if ("Y".equalsIgnoreCase(source) || "T".equalsIgnoreCase(source)
            || "Yes".equalsIgnoreCase(source) || "1".equalsIgnoreCase(source))
      {
         return Boolean.TRUE;
      }
      else if ("N".equals(source) || "F".equals(source) || "No".equals(source)
            || "0".equalsIgnoreCase(source))
      {
         return Boolean.FALSE;
      }
      return Boolean.valueOf(source);
   }

   /**
    * 
    * @param <T>
    * @param source
    * @param targetType
    * @return
    * @throws NoSuchMethodException
    */
   @SuppressWarnings("unchecked")
   public static <T> T getTypeViaValueOfMethod(final String source, final Class<T> targetType)
         throws NoSuchMethodException
   {
      Class<?> actualTarget = targetType;
      /*
       * if this is a primitive type, use the Object class's "valueOf()" 
       * method.
       */
      if (targetType.isPrimitive())
      {
         actualTarget = PRIMITIVES.get(targetType);
      }
      T result = null;
      try
      {
         // if the type has a static "valueOf()" method, try and create the instance that way
         Method valueOf = actualTarget.getDeclaredMethod("valueOf", new Class[]
         {String.class});
         Object value = valueOf.invoke(null, source);
         if (actualTarget.equals(targetType) && targetType.isInstance(value))
         {
            result = targetType.cast(value);
         }
         /*
          * handle the primitive case
          */
         else if (!actualTarget.equals(targetType) && actualTarget.isInstance(value))
         {
            // because you can't use targetType.cast() with primitives.
            result = (T) value;
         }
      }
      catch (IllegalAccessException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ExceptionAdapter(e);
      }
      return result;
   }

   /**
    * 
    * @param <T>
    * @param source
    * @param targetType
    * @return
    * @throws IllegalArgumentException
    * @throws InstantiationException
    * @throws IllegalAccessException
    * @throws InvocationTargetException
    */
   private static <T> T getTypeViaStringConstructor(String source, Class<T> targetType)
   {
      T result = null;
      Constructor<T> c = null;

      try
      {
         c = targetType.getDeclaredConstructor(new Class[]
         {String.class});
      }
      catch (NoSuchMethodException e)
      {
         String msg = new StringBuilder().append(targetType.getName()).append(
               " has no String constructor").toString();
         throw new IllegalArgumentException(msg, e);
      }
      
      try
      {
         result = c.newInstance(new Object[] {source});
      }
      catch (InstantiationException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IllegalAccessException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ExceptionAdapter(e);
      }
      return result;
   }
}
