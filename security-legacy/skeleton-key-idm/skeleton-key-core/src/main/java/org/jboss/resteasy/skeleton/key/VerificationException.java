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

   public VerificationException(String message)
   {
      super(message);
   }

   public VerificationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public VerificationException(Throwable cause)
   {
      super(cause);
   }
}
