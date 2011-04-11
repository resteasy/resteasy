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
    * Use this key alias to find public key to verify with.
    *
    * @return
    */
   String keyAlias() default "";

   /**
    * If keyAlias() is not set, the attributeKeyAlias() will be used to determine the key alias.
    * So, if this annotation attribute's value is, let's say, "signer", then the key alias will be the value of
    * the signer attribute embedded in the transmitted Content-Signature header value.
    * <p/>
    * The default value of this attribute is "d", the domain identifier.
    *
    * @return
    */
   String attributeKeyAlias() default "d";

   /**
    * Expiration check based on expiration attribute will be done unless this flag is set to false.
    *
    * @return
    */
   boolean ignoreExpiration() default false;

   /**
    * Do a stale check if a timestamp attribute is preset.
    *
    * @return
    */
   After stale() default @After;

}
