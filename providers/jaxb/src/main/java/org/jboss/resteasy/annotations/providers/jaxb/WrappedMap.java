package org.jboss.resteasy.annotations.providers.jaxb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this on a method or parameter when you want to marshal or unmarshal a map of JAXB objects
 * <p>
 * i.e.
 * <pre>
 * {@literal @}PUT
 * {@literal @}Consumes("application/xml") public void put({@literal @}WrappedMap Map{@literal <}String, User{@literal >} users);
 * </pre>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WrappedMap
{
   /**
    * map element name
    */
   String map() default "map";

   /**
    * entry element name 
    * @return entry
    */
   String entry() default "entry";

   /**
    * entry's key attribute name
    * @return key
    */
   String key() default "key";

   String namespace() default "";

   String prefix() default "";
}