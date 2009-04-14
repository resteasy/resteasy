package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Bad Request (400) is encountered
 */
public class BadRequestException extends Failure
{

   public BadRequestException(String s)
   {
      super(s, 400);
   }

   public BadRequestException(String s, Response response)
   {
      super(s, response);
   }

   public BadRequestException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public BadRequestException(String s, Throwable throwable)
   {
      super(s, throwable, 400);
   }

   public BadRequestException(Throwable throwable)
   {
      super(throwable, 400);
   }

   public BadRequestException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }


}