package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyIOException extends ResteasyClientException
{
   private static final long serialVersionUID = -5711578608757689465L;

   public ResteasyIOException()
   {
   }

   public ResteasyIOException(final String message)
   {
      super(message);
   }

   public ResteasyIOException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ResteasyIOException(final Throwable cause)
   {
      super(cause);
   }
}
