package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Method Not Allowed (405) is encountered
 * JAX-RS now has this exception
 *
 * @deprecated Replaced by javax.ws.rs.NotAllowedException in jaxrs-api module.
 *
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.NotAllowedException
 */
@Deprecated
public class MethodNotAllowedException extends LoggableFailure
{

   public MethodNotAllowedException(final String s)
   {
      super(s, 405);
   }

   public MethodNotAllowedException(final String s, final Response response)
   {
      super(s, response);
   }

   public MethodNotAllowedException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public MethodNotAllowedException(final String s, final Throwable throwable)
   {
      super(s, throwable, 405);
   }

   public MethodNotAllowedException(final Throwable throwable)
   {
      super(throwable, 405);
   }

   public MethodNotAllowedException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }


}
