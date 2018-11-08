package org.jboss.resteasy.spi;

/**
 * This exception should only be used by Resteasy integrators.  Applications code should use WebApplicationException
 * <p>
 * Thrown by dispatcher when it can't handle a particular exception
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UnhandledException extends RuntimeException
{
   public UnhandledException(final String s, final Throwable throwable)
   {
      super(s, throwable);
   }

   public UnhandledException(final Throwable throwable)
   {
      super(throwable);
   }
}
