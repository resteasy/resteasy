package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Any exception thrown by a MessageBodyReader chain that is not an instance of a ReaderException is caught internally
 * by the Resteasy runtime and wrapped with an instance of ReaderException.
 *
 * If you want to have special exception handling for exceptions thrown by MessageBodyReaders, then write an exception
 * mapper for ReaderException.
 *
 * Also, you may extend this class and throw instances of it from your MessageBodyReaders (and interceptors)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderException extends Failure
{
   public ReaderException(final String s, final Response response)
   {
      super(s, response);
   }

   public ReaderException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public ReaderException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }

   public ReaderException(final String s, final Throwable throwable)
   {
      super(s, throwable, -1);
   }

   public ReaderException(final Throwable throwable)
   {
      super(throwable, -1);
   }

   public ReaderException(final String s)
   {
      super(s, -1);
   }

   public ReaderException(final int errorCode)
   {
      super(errorCode);
   }

   public ReaderException(final String s, final int errorCode)
   {
      super(s, errorCode);
   }

   public ReaderException(final String s, final Throwable throwable, final int errorCode)
   {
      super(s, throwable, errorCode);
   }

   public ReaderException(final Throwable throwable, final int errorCode)
   {
      super(throwable, errorCode);
   }
}
