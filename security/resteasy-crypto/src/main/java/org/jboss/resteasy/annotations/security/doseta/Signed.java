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
 * Private keys are never discovered via DNS.
 * <p/>
 * The private key used to sign is discovered in the KeyRepository via an alias of
 * (selector + ".")? + "_domainKey." + domain - Same as the doseta specification
 * <p/>
 * <p/>
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
   String algorithm() default "";

   /**
    * If there is no domain, then abort.
    * <p/>
    * If not set, the runtime may set a default domain.  See documentation for details.
    *
    * @return
    */
   String domain() default "";

   /**
    * A default may be used if not set.  See documentation for more details.
    *
    * @return
    */
   String selector() default "";

   /**
    * Will calculate and add a timestamp
    *
    * @return
    */
   boolean timestamped() default false;


   After expires() default @After;


}
