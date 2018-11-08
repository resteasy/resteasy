package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyHttpHostConnectException extends ResteasyIOException
{
   private static final long serialVersionUID = -5711578608757689465L;

   public ResteasyHttpHostConnectException()
   {
   }

   public ResteasyHttpHostConnectException(final String message)
   {
      super(message);
   }

   public ResteasyHttpHostConnectException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ResteasyHttpHostConnectException(final Throwable cause)
   {
      super(cause);
   }
}
