package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.spi.LoggableFailure;

/**
 * Thrown by RESTEasy when HTTP Unauthorized (401) is encountered
 */
public class UnauthorizedSignatureException extends LoggableFailure
{
   protected VerificationResults results;

   public UnauthorizedSignatureException(VerificationResults results)
   {
      super(failedVerifierMessage(results), 401);
      this.results = results;
   }

   public static String failedVerifierMessage(VerificationResults results)
   {
      StringBuffer msg = new StringBuffer("Failed to verify signatures:");
      for (VerificationResultSet set : results.getResults())
      {
         for (VerificationResult result : set.getResults())
         {
            msg.append("\r\n");
            if (result.getFailureReason() != null)
            {
               msg.append(result.getFailureReason());
            }
            if (result.getFailureException() != null)
            {
               msg.append(" ");
               msg.append(result.getFailureException().getMessage());
            }
         }
      }
      return msg.toString();
   }

   public VerificationResults getResults()
   {
      return results;
   }
}