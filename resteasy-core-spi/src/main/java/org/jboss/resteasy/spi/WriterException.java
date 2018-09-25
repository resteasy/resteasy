package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Any exception thrown by a MessageBodyWriter chain that is not an instance of a WriterException is caught internally
 * by the Resteasy runtime and wrapped with an instance of WriterException.
 *
 * If you want to have special exception handling for exceptions thrown by MessageBodyWriters and their interceptors, then write an exception
 * mapper for WriterException.
 *
 * Also, you may extend this class and throw instances of it from your MessageBodyWriters (and interceptors)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WriterException extends LoggableFailure
{
   public WriterException(final String s, final Response response)
   {
      super(s, response);
   }

   public WriterException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public WriterException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }

   public WriterException(final String s, final Throwable throwable)
   {
      super(s, throwable, -1);
   }

   public WriterException(final Throwable throwable)
   {
      super(throwable, -1);
   }

   public WriterException(final String s)
   {
      super(s, -1);
   }

   public WriterException(final int errorCode)
   {
      super(errorCode);
   }

   public WriterException(final String s, final int errorCode)
   {
      super(s, errorCode);
   }

   public WriterException(final String s, final Throwable throwable, final int errorCode)
   {
      super(s, throwable, errorCode);
   }

   public WriterException(final Throwable throwable, final int errorCode)
   {
      super(throwable, errorCode);
   }
}
