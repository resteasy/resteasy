package org.jboss.resteasy.security.doseta;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Verifier
{
   protected KeyRepository repository;
   protected List<Verification> verifications = new ArrayList<Verification>();

   public KeyRepository getRepository()
   {
      return repository;
   }

   public void setRepository(KeyRepository repository)
   {
      this.repository = repository;
   }

   public Verification addNew()
   {
      Verification verification = new Verification();
      verifications.add(verification);
      return verification;
   }

   public List<Verification> getVerifications()
   {
      return verifications;
   }

   public VerificationResults verify(List<DosetaSignature> signatures, Map headers, byte[] body)
   {
      VerificationResults results = new VerificationResults();
      results.setVerified(true);
      for (Verification verification : verifications)
      {
         VerificationResultSet resultSet = new VerificationResultSet();
         results.getResults().add(resultSet);
         resultSet.setVerification(verification);

         List<DosetaSignature> matched = new ArrayList<DosetaSignature>();
         matched.addAll(signatures);
         Iterator<DosetaSignature> iterator = matched.iterator();


         while (iterator.hasNext())
         {
            DosetaSignature sig = iterator.next();
            if (verification.getIdentifierName() != null)
            {
               String value = sig.getAttributes().get(verification.getIdentifierName());
               if (value == null || !value.equals(verification.getIdentifierValue()))
               {
                  iterator.remove();
                  continue;
               }
            }
         }

         // could not find a signature to match verification
         if (matched.isEmpty())
         {
            results.setVerified(false);
            continue;
         }

         resultSet.setVerified(true);
         for (DosetaSignature signature : matched)
         {
            VerificationResult result = verifySignature(headers, body, verification, signature);
            resultSet.getResults().add(result);
            if (result.isVerified() == false)
            {
               resultSet.setVerified(false);
               results.setVerified(false);
            }
         }

      }
      return results;
   }


   public VerificationResult verifySignature(Map headers, byte[] body, Verification verification, DosetaSignature signature)
   {
      VerificationResult result = new VerificationResult();
      result.setSignature(signature);


      PublicKey key = verification.getKey();

      if (key == null)
      {
         if (verification.getRepository() != null)
         {
            key = verification.getRepository().findPublicKey(signature);
         }
         else if (repository != null)
         {
            key = repository.findPublicKey(signature);
         }
         if (key == null)
         {
            result.setFailureReason("Could not find PublicKey for DKIMSignature " + signature);
            return result;
         }
      }

      boolean verified = false;
      try
      {
         verified = signature.verify(headers, body, key);
      }
      catch (Exception e)
      {
         result.setFailureException(e);
         return result;
      }
      if (verified == false)
      {
         result.setFailureReason("Signature verification failed");
         return result;
      }
      if (verification.isIgnoreExpiration() == false)
      {
         if (signature.isExpired())
         {
            result.setFailureReason("Signature expired");
            return result;
         }
      }
      if (verification.isStaleCheck())
      {
         if (signature.isStale(verification.getStaleSeconds(),
                 verification.getStaleMinutes(),
                 verification.getStaleHours(),
                 verification.getStaleDays(),
                 verification.getStaleMonths(),
                 verification.getStaleYears()))
         {
            result.setFailureReason("Signature is stale");
            return result;
         }
      }
      result.setVerified(true);
      return result;
   }

}
