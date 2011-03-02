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
    * Verify a signature in a Content-Signature header that is signed by this attribute
    *
    * @return
    */
   String id() default "";

   /**
    * Verify a signature in a Content-Signature header that is signed by this attribute
    *
    * @return
    */
   String signer() default "";

   /**
    * Use this key alias to find public key to verify with. The default is to use the signer() attribute
    *
    * @return
    */
   String useKey() default "";

   boolean ignoreExpiration() default false;

   After stale() default @After;

}
