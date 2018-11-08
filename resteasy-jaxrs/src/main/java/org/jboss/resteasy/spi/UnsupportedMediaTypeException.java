package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Unsupported Media Type (415) is encountered
 * JAX-RS now has this exception
 *
 * @deprecated Replaced by javax.ws.rs.NotSupportedException in jaxrs-api module.
 *
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.NotSupportedException
 */
@Deprecated
public class UnsupportedMediaTypeException extends LoggableFailure
{

   public UnsupportedMediaTypeException(final String s)
   {
      super(s, 415);
   }

   public UnsupportedMediaTypeException(final String s, final Response response)
   {
      super(s, response);
   }

   public UnsupportedMediaTypeException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public UnsupportedMediaTypeException(final String s, final Throwable throwable)
   {
      super(s, throwable, 415);
   }

   public UnsupportedMediaTypeException(final Throwable throwable)
   {
      super(throwable, 415);
   }

   public UnsupportedMediaTypeException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }


}
