package org.jboss.resteasy.spi;

/**
 * This exception should only be used by Resteasy integrators.  Application code should use WebApplicationException
 * An exception thrown by the application.
 * <p/>
 * We don't reuse WebApplicationException as it provides a Response object.  This exception means that the Resteasy
 * runtime must handle the exception.  i.e. Find an ExceptionMapper to map it to a Response, or let some interceptor
 * handle it in a special way.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationException extends RuntimeException
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
