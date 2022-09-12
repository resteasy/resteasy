package org.jboss.resteasy.spi;

import jakarta.ws.rs.core.Response;

/**
 * This exception is thrown when the client invokes HTTP OPTIONS operation and the JAX-RS resource
 * does not have a Java method that supports OPTIONS.  RESTEasy provides a default behavior for OPTIONS.
 * If you want to override this behavior, write an exception mapper for this exception.
 */
public class DefaultOptionsMethodException extends Failure
{
   public DefaultOptionsMethodException(final String s, final Response response)
   {
      super(s, response);
   }
}
