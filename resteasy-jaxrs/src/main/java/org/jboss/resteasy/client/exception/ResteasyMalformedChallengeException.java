package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyMalformedChallengeException extends ResteasyProtocolException
{
   private static final long serialVersionUID = -5711578608757689465L;

   public ResteasyMalformedChallengeException()
   {
   }

   public ResteasyMalformedChallengeException(final String message)
   {
      super(message);
   }

   public ResteasyMalformedChallengeException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ResteasyMalformedChallengeException(final Throwable cause)
   {
      super(cause);
   }
}
