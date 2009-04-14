package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Internal Service Error (500) is encountered
 */
public class InternalServerErrorException extends LoggableFailure
{

   public InternalServerErrorException(String s)
   {
      super(s, 500);
   }

   public InternalServerErrorException(String s, Response response)
   {
      super(s, response);
   }

   public InternalServerErrorException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public InternalServerErrorException(String s, Throwable throwable)
   {
      super(s, throwable, 500);
   }

   public InternalServerErrorException(Throwable throwable)
   {
      super(throwable, 500);
   }

   public InternalServerErrorException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }


}