package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * This exception should only be used by Resteasy integrators.  Applications code should use WebApplicationException
 * <p>
 * This is thrown by Resteasy runtime when a failure occurs.  It will be logged by the runtime.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LoggableFailure extends Failure
{
   public LoggableFailure(final String s, final Response response)
   {
      super(s, response);
   }

   public LoggableFailure(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public LoggableFailure(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }

   public LoggableFailure(final String s, final Throwable throwable)
   {
      super(s, throwable);
      loggable = true;
   }

   public LoggableFailure(final Throwable throwable)
   {
      super(throwable);
      loggable = true;
   }

   public LoggableFailure(final String s)
   {
      super(s);
      loggable = true;
   }

   public LoggableFailure(final int errorCode)
   {
      super(errorCode);
      loggable = true;
   }

   public LoggableFailure(final String s, final int errorCode)
   {
      super(s, errorCode);
      loggable = true;
   }

   public LoggableFailure(final String s, final Throwable throwable, final int errorCode)
   {
      super(s, throwable, errorCode);
      loggable = true;
   }

   public LoggableFailure(final Throwable throwable, final int errorCode)
   {
      super(throwable, errorCode);
      loggable = true;
   }
}
