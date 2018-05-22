package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Unauthorized (403) is encountered
 * 
 * @deprecated Replaced by javax.ws.rs.ForbiddenException in jaxrs-api module.
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.ForbiddenException
 */
@Deprecated
public class ForbiddenException extends LoggableFailure
{
   public ForbiddenException()
   {
      super(403);
   }

   public ForbiddenException(String s)
   {
      super(s, 403);
   }

   public ForbiddenException(String s, Response response)
   {
      super(s, response);
   }

   public ForbiddenException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public ForbiddenException(String s, Throwable throwable)
   {
      super(s, throwable, 403);
   }

   public ForbiddenException(Throwable throwable)
   {
      super(throwable, 403);
   }

   public ForbiddenException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }


}