package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Internal Service Error (500) is encountered
 */
public class InternalServerErrorException extends LoggableFailure
{

   public InternalServerErrorException(final String s)
   {
      super(s, 500);
   }

   public InternalServerErrorException(final String s, final Response response)
   {
      super(s, response);
   }

   public InternalServerErrorException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public InternalServerErrorException(final String s, final Throwable throwable)
   {
      super(s, throwable, 500);
   }

   public InternalServerErrorException(final Throwable throwable)
   {
      super(throwable, 500);
   }

   public InternalServerErrorException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }


}
