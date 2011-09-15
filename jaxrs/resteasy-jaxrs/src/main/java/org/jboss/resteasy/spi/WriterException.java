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
   public WriterException(String s, Response response)
   {
      super(s, response);
   }

   public WriterException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public WriterException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }

   public WriterException(String s, Throwable throwable)
   {
      super(s, throwable, -1);
   }

   public WriterException(Throwable throwable)
   {
      super(throwable, -1);
   }

   public WriterException(String s)
   {
      super(s, -1);
   }

   public WriterException(int errorCode)
   {
      super(errorCode);
   }

   public WriterException(String s, int errorCode)
   {
      super(s, errorCode);
   }

   public WriterException(String s, Throwable throwable, int errorCode)
   {
      super(s, throwable, errorCode);
   }

   public WriterException(Throwable throwable, int errorCode)
   {
      super(throwable, errorCode);
   }
}