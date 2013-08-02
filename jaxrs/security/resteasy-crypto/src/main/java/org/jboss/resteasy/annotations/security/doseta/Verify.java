package org.jboss.resteasy.annotations.security.doseta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verification of input signature specified in a signature header.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Verify
{
   /**
    * If there are multiple signature headers, use this attribute name to pick which signature to verify
    *
    * @return
    */
   String identifierName() default "";

   /**
    * What should be the value of identifierName()
    *
    * @return
    */
   String identifierValue() default "";

   /**
    * Expiration check based on expiration attribute will be done unless this flag is set to false.
    *
    * @return
    */
   boolean ignoreExpiration() default false;

   /**
    * If message body exists, are we required to check the hash of it?
    *
    * @return
    */
   boolean bodyHashRequired() default true;

   /**
    * Do a stale check if a timestamp attribute is preset.
    *
    * @return
    */
   After stale() default @After;

}
