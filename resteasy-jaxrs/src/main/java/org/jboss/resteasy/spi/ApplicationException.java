package org.jboss.resteasy.spi;

/**
 * This exception is thrown internally by Resteasy runtime.  Any server exception thrown by jaxrs resource method code will be caught
 * and wrapped by this exception.  If you want to catch all exceptions thrown by jaxrs resource methods, write an exception
 * mapper for ApplicationException.
 *
 * This exception should only be used by Resteasy integrators.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public final class ApplicationException extends RuntimeException
{
   public ApplicationException(String s, Throwable throwable)
   {
      super(s, throwable);
   }

   public ApplicationException(Throwable throwable)
   {
      super(throwable);
   }
}
