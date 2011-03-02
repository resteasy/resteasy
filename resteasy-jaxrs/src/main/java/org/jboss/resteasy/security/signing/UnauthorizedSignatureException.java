package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.spi.LoggableFailure;

/**
 * Thrown by RESTEasy when HTTP Unauthorized (401) is encountered
 */
public class UnauthorizedSignatureException extends LoggableFailure
{
   protected Verifier verifier;

   public UnauthorizedSignatureException(Verifier verifier)
   {
      super(failedVerifierMessage(verifier), 401);
      this.verifier = verifier;
   }

   public static String failedVerifierMessage(Verifier verifier)
   {
      StringBuffer msg = new StringBuffer("Failed to verify signatures:");
      for (Verification verification : verifier.getVerifications())
      {
         if (verification.isVerified() == false)
         {
            fillMessage(msg, verification);
         }

      }
      return msg.toString();
   }

   private static void fillMessage(StringBuffer msg, Verification verification)
   {
      if (verification.getFailureReason() != null)
      {
         msg.append(" ");
         msg.append(verification.getFailureReason());
      }
      if (verification.getFailureException() != null)
      {
         msg.append(" ");
         msg.append(verification.getFailureException().getMessage());
      }
   }

   public Verifier getVerifier()
   {
      return verifier;
   }
}