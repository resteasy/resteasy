package org.jboss.resteasy.annotations.security.signature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convenience annotation that triggers the signing of a request or response.  Will create a Content-Signature header.
 * Algorithm will be included automatically as an attribute (but not as part of signature)
 * Signer will be included automatically as an attribute and part of signature unless the transmitSigner is set to false
 * Timestamp will be included automatically as an attribute and part of signature if timestamped() is set to true
 * An expiration attribute will be added to the signature if expires() is set.
 * <p/>
 * If you want more fine-grain control over what attributes are displayed within Content-Signature or included as
 * part of the signature, then you must create your own ContentSignatures and pass it with the request or response
 * you want to sign.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Signed
{
   /**
    * Algorithm to use when signing.  For verification, Resteasy will look inside Content-Signature header first
    * before using the value of this annotation.
    *
    * @return
    */
   String algorithm() default "";

   /**
    * Set the id attribute and add it to the signature.  If neither signer() nor useKey() is set, this will be used
    * as the alias to find the private key to sign with.
    */
   String id() default "";

   /**
    * Set the signer attribute and add it to the signature.  If useKey() isn't set, then this will be the default
    * alias to use to look up the private key to sign with.
    *
    * @return
    */
   String signer() default "";

   /**
    * Key alias to use to lookup a key in the KeyRepository.  This does not add any metadata to the Content-Signature header.
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
