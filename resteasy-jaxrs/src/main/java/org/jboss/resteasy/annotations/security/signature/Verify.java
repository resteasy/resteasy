package org.jboss.resteasy.annotations.security.signature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verification of input signature specified in Content-Signature header.  Content-Signature attribute values take
 * precedence over any value within this annotation.  If id() is not specified, then signatures will be looked up via
 * the signer().
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Verify
{
   /**
    * Algorithm to use when verifying.
    *
    * @return
    */
   String algorithm() default "";

   /**
    * Verify a signature that has this id attribute set.
    *
    * @return
    */
   String id() default "";

   /**
    * Verify a signature that has this signer attribute set.
    *
    * @return
    */
   String signer() default "";

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
    *
    * The default value of this attribute is "signer".
    *
    * @return
    */
   String attributeKeyAlias() default "signer";

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
