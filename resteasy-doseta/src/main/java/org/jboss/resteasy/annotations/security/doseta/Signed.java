package org.jboss.resteasy.annotations.security.doseta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convenience annotation that triggers the signing of a request or response using the DOSETA specification.
 * Will create a Doseta-Signature header.
 * <p/>
 * By default simple canonicalization will be used for both header and body.
 * <p/>
 * The private key used to sign is discovered using a KeyRepository.  The name used to lookup the public key is as follows
 * <p/>
 * - keyAlias() if it is set
 * - domain() if it is set
 * <p/>
 * <p/>
 * If you want more fine-grain control over the signature header
 * then you must create your own DosetaSignature instances and pass it with the request or response
 * you want to sign.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @See org.jboss.resteasy.security.keys.KeyRepository
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Signed
{
   /**
    * Algorithm to use when signing.  For verification, Resteasy will look inside signature header first
    * before using the value of this annotation.
    *
    * @return
    */
   String algorithm() default "";

   String domain() default "";

   /**
    * Key alias to use to lookup a key in the KeyRepository.  This does not add any metadata to the signature header.
    *
    * @return
    */
   String keyAlias() default "";

   /**
    * Will calculate and add a timestamp
    *
    * @return
    */
   boolean timestamped() default false;


   After expires() default @After;


}
