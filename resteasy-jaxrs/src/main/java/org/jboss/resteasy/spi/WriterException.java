package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * MessageBodyWriter writeTo() exception
 * <p/>
 * If you do not provide an error code or Response, on the server side it will default to 500 response code.
 * If you provide a throwable, that exception will be matched against an ExceptionMapper first.
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