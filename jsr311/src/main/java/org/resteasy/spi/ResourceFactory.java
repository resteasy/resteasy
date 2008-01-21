package org.resteasy.spi;

/**
 * Factory that creates or find a target resource to invoke on.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceFactory
{
   Object createResource(HttpInput input, HttpOutput output);

   /**
    * Class to scan for jax-rs annotations
    *
    * @return
    */
   Class<?> getScannableClass();
}
