package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyConnectTimeoutException extends ResteasyIOException
{
   private static final long serialVersionUID = -5711578608757689465L;

   public ResteasyConnectTimeoutException()
   {
   }

   public ResteasyConnectTimeoutException(final String message)
   {
      super(message);
   }

   public ResteasyConnectTimeoutException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ResteasyConnectTimeoutException(final Throwable cause)
   {
      super(cause);
   }
}
