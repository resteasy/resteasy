package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Not Found (404) is encountered
 *
 * JAX-RS now has this exception
 *
 * @deprecated Replaced by javax.ws.rs.NotFoundException in jaxrs-api module.
 *
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.NotFoundException
 */
@Deprecated
public class NotFoundException extends Failure
{

   public NotFoundException(final String s)
   {
      super(s, 404);
   }

   public NotFoundException(final String s, final Response response)
   {
      super(s, response);
   }

   public NotFoundException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public NotFoundException(final String s, final Throwable throwable)
   {
      super(s, throwable, 404);
   }

   public NotFoundException(final Throwable throwable)
   {
      super(throwable, 404);
   }

   public NotFoundException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }


}
