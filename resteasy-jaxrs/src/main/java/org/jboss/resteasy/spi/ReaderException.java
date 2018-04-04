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
   public ReaderException(String s, Response response)
   {
      super(s, response);
   }

   public ReaderException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public ReaderException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }

   public ReaderException(String s, Throwable throwable)
   {
      super(s, throwable, -1);
   }

   public ReaderException(Throwable throwable)
   {
      super(throwable, -1);
   }

   public ReaderException(String s)
   {
      super(s, -1);
   }

   public ReaderException(int errorCode)
   {
      super(errorCode);
   }

   public ReaderException(String s, int errorCode)
   {
      super(s, errorCode);
   }

   public ReaderException(String s, Throwable throwable, int errorCode)
   {
      super(s, throwable, errorCode);
   }

   public ReaderException(Throwable throwable, int errorCode)
   {
      super(throwable, errorCode);
   }
}
