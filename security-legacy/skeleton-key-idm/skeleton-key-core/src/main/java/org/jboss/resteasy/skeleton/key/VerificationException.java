package org.jboss.resteasy.skeleton.key;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VerificationException extends Exception
{
   public VerificationException()
   {
   }

   public VerificationException(final String message)
   {
      super(message);
   }

   public VerificationException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public VerificationException(final Throwable cause)
   {
      super(cause);
   }
}
