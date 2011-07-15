package org.jboss.resteasy.security.doseta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VerificationResults
{
   protected boolean verified;
   protected List<VerificationResultSet> results = new ArrayList<VerificationResultSet>();

   public boolean isVerified()
   {
      return verified;
   }

   public void setVerified(boolean verified)
   {
      this.verified = verified;
   }

   public VerificationResultSet getResultSet(Verification verification)
   {
      for (VerificationResultSet set : results)
      {
         if (set.getVerification().equals(verification)) return set;
      }
      return null;
   }

   public VerificationResult getFirstResult(Verification verification)
   {
      VerificationResultSet set = getResultSet(verification);
      if (set == null) return null;
      return set.getResults().get(0);
   }

   public List<VerificationResultSet> getResults()
   {
      return results;
   }

   public void setResults(List<VerificationResultSet> results)
   {
      this.results = results;
   }

   @Override
   public String toString()
   {
      return "VerificationResults{" +
              "verified=" + verified +
              ", results=" + results +
              '}';
   }
}
