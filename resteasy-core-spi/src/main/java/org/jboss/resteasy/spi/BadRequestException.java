package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Bad Request (400) is encountered
 *
 * @deprecated This class is deprecated use javax.ws.rs.BadRequestException
 */
@Deprecated
public class BadRequestException extends Failure
{

   public BadRequestException(final String s)
   {
      super(s, 400);
   }

   public BadRequestException(final String s, final Response response)
   {
      super(s, response);
   }

   public BadRequestException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public BadRequestException(final String s, final Throwable throwable)
   {
      super(s, throwable, 400);
   }

   public BadRequestException(final Throwable throwable)
   {
      super(throwable, 400);
   }

   public BadRequestException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }


}
