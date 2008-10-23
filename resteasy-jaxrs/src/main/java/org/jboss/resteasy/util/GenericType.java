package org.jboss.resteasy.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GenericType<T>
{
   final Class<T> type;
   final Type genericType;

   /**
    * Constructs a new generic entity. Derives represented class from type
    * parameter. Note that this constructor is protected, users should create
    * a (usually anonymous) subclass as shown above.
    *
    * @param entity the entity instance, must not be null
    * @throws IllegalArgumentException if entity is null
    */
   protected GenericType()
   {
      Type superclass = getClass().getGenericSuperclass();
      if (!(superclass instanceof ParameterizedType))
      {
         throw new RuntimeException("Missing type parameter.");
      }
      ParameterizedType parameterized = (ParameterizedType) superclass;
      this.genericType = parameterized.getActualTypeArguments()[0];
      this.type = (Class<T>) Types.getRawType(genericType);
   }

   /**
    * Gets the raw type of the enclosed entity. Note that this is the raw type of
    * the instance, not the raw type of the type parameter. I.e. in the example
    * in the introduction, the raw type is {@code ArrayList} not {@code List}.
    *
    * @return the raw type
    */
   public final Class<T> getType()
   {
      return type;
   }

   /**
    * Gets underlying {@code Type} instance. Note that this is derived from the
    * type parameter, not the enclosed instance. I.e. in the example
    * in the introduction, the type is {@code List<String>} not
    * {@code ArrayList<String>}.
    *
    * @return the type
    */
   public final Type getGenericType()
   {
      return genericType;
   }

}