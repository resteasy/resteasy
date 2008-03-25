package org.resteasy.spi;

/**
 * Implementations of this interface are registered through the Registry class.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceReference
{
   /**
    * Class to scan for jax-rs annotations
    *
    * @return
    */
   Class<?> getScannableClass();

   /**
    * This returns a path specific factory.
    * <p/>
    * getFactory() will be invoked for every resource method binding there is at deployment time.  So, if you're
    * a singleton factory, then this will be invoked for every resource method there is in your singleton class.
    *
    * @param factory
    * @return
    */
   ResourceFactory getFactory(InjectorFactory factory);
}
