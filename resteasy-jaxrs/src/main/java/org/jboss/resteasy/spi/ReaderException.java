package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * MessageBodyReader readFrom() exception
 * <p/>
 * If you do not provide an error code or Response, on the server side it will default to 400 response code.
 * If you provide a throwable, that exception will be matched against an ExceptionMapper first.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderException extends LoggableFailure
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
