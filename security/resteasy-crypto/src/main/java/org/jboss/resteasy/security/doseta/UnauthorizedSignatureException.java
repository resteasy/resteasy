package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.spi.ReaderException;

/**
 * Thrown by RESTEasy when HTTP Unauthorized (401) is encountered
 */
public class UnauthorizedSignatureException extends ReaderException
{
   protected VerificationResults results;

   public UnauthorizedSignatureException(String reason)
   {
      super(reason, 401);
   }

   public UnauthorizedSignatureException(VerificationResults results)
   {
      super(failedVerifierMessage(results), 401);
      this.results = results;
   }

   public static String failedVerifierMessage(VerificationResults results)
   {
      StringBuffer msg = new StringBuffer(Messages.MESSAGES.failedToVerifySignatures());
      for (VerificationResultSet set : results.getResults())
      {
         for (VerificationResult result : set.getResults())
         {
            msg.append("\r\n");
            if (result.getFailureException() != null)
            {
               msg.append(" ");
               msg.append(result.getFailureException().getLocalizedMessage());
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