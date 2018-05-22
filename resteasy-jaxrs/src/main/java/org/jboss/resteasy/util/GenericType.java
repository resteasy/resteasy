package org.jboss.resteasy.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * This class is a trick used to extract GenericType information at runtime.  Java does not allow you get generic
 * type information easily, so this class does the trick.  For example:
 * <pre>
 * Type genericType = (new GenericType&#x3C;List&#x3C;String&#x3E;&#x3E;() {}).getGenericType();
 * </pre>
 * <p>
 * The above code will get you the genericType for List&#x3C;String&#x3E;
 * 
 * N.B. This class is replaced by javax.ws.rs.core.GenericType.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated Replaced by javax.ws.rs.core.GenericType
 * 
 * @see javax.ws.rs.core.GenericType
 */
@Deprecated
public class GenericType<T>
{
   final Class<T> type;
   final Type genericType;

   /**
    * Constructs a new generic entity. Derives represented class from type
    * parameter. Note that this constructor is protected, users should create
    * a (usually anonymous) subclass as shown above.
    */
   protected GenericType()
   {
      Type superclass = getClass().getGenericSuperclass();
      if (!(superclass instanceof ParameterizedType))
      {
         throw new RuntimeException(Messages.MESSAGES.missingTypeParameter());
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