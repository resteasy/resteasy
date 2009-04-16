package org.jboss.resteasy.annotations.providers.jaxb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this on a method or parameter when you want to marshal or unmarshal a map of JAXB objects
 * <p/>
 * i.e.
 * <p/>
 * <pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @PUT
 * @Consumes("application/xml") public void put(@WrappedMap Map<String, User> users);
 * <p/>
 * </pre>
 * <p/>
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
    * entry element name *
    */
   String entry() default "entry";

   /**
    * entry's key attribute name
    */
   String key() default "key";

   String namespace() default "";

   String prefix() default "";
}