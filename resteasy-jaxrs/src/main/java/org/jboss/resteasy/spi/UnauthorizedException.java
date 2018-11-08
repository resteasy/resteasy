package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Unauthorized (401) is encountered
 *
 * @deprecated Replaced by javax.ws.rs.NotAuthorizedException in jaxrs-api module.
 *
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.NotAuthorizedException
 */
@Deprecated
public class UnauthorizedException extends LoggableFailure
{
   public UnauthorizedException()
   {
      super(401);
   }

   public UnauthorizedException(final String s)
   {
      super(s, 401);
   }

   public UnauthorizedException(final String s, final Response response)
   {
      super(s, response);
   }

   public UnauthorizedException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public UnauthorizedException(final String s, final Throwable throwable)
   {
      super(s, throwable, 401);
   }

   public UnauthorizedException(final Throwable throwable)
   {
      super(throwable, 401);
   }

   public UnauthorizedException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }


}
