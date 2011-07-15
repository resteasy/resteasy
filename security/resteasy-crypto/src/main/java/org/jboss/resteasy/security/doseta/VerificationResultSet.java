package org.jboss.resteasy.security.doseta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VerificationResultSet
{
   private boolean verified;
   private Verification verification;
   private List<VerificationResult> results = new ArrayList<VerificationResult>();

   public boolean isVerified()
   {
      return verified;
   }

   public void setVerified(boolean verified)
   {
      this.verified = verified;
   }

   public Verification getVerification()
   {
      return verification;
   }

   public void setVerification(Verification verification)
   {
      this.verification = verification;
   }

   public List<VerificationResult> getResults()
   {
      return results;
   }

   public void setResults(List<VerificationResult> results)
   {
      this.results = results;
   }
}
