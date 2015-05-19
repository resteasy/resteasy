package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Not Acceptable (406) is encountered
 * JAX-RS now has this exception
 */
@Deprecated
public class NotAcceptableException extends LoggableFailure
{
   public NotAcceptableException(String s)
   {
      super(s, 406);
   }

   public NotAcceptableException(String s, Response response)
   {
      super(s, response);
   }

   public NotAcceptableException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public NotAcceptableException(String s, Throwable throwable)
   {
      super(s, throwable, 406);
   }

   public NotAcceptableException(Throwable throwable)
   {
      super(throwable, 406);
   }

   public NotAcceptableException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }

}