package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyClientException extends RuntimeException
{
   private static final long serialVersionUID = -5711578608757689465L;

   public ResteasyClientException()
   {
   }

   public ResteasyClientException(final String message)
   {
      super(message);
   }

   public ResteasyClientException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ResteasyClientException(final Throwable cause)
   {
      super(cause);
   }
}
