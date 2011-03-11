package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.security.keys.KeyRepository;

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

   public VerificationResults verify(ContentSignatures signatures, Map headers, byte[] body)
   {
      VerificationResults results = new VerificationResults();
      results.setVerified(true);
      for (Verification verification : verifications)
      {
         VerificationResultSet resultSet = new VerificationResultSet();
         results.getResults().add(resultSet);
         resultSet.setVerification(verification);

         String id = verification.getId();
         String signer = verification.getSigner();

         List<ContentSignature> matched = new ArrayList<ContentSignature>();
         matched.addAll(signatures.getSignatures());
         Iterator<ContentSignature> iterator = matched.iterator();
         while (iterator.hasNext())
         {
            ContentSignature sig = iterator.next();
            if (id != null && !id.equals(sig.getId()))
            {
               iterator.remove();
               continue;
            }
            if (signer != null && !signer.equals(sig.getSigner()))
            {
               iterator.remove();
               continue;
            }
         }

         // could not find a signature to match verification
         if (matched.isEmpty())
         {
            results.setVerified(false);
            continue;
         }

         resultSet.setVerified(true);
         for (ContentSignature signature : matched)
         {
            VerificationResult result = verifySignature(signatures, headers, body, verification, signature);
            resultSet.getResults().add(result);
            if(result.isVerified() == false)
            {
               resultSet.setVerified(false);
               results.setVerified(false);
            }
         }

      }
      return results;
   }


   public VerificationResult verifySignature(ContentSignatures signatures, Map headers, byte[] body, Verification verification, ContentSignature signature)
   {
      VerificationResult result = new VerificationResult();
      result.setSignature(signature);


      PublicKey key = verification.getKey();

      if (key == null)
      {
         String keyAlias = null;
         if (verification.getKeyAlias() != null) keyAlias = verification.getKeyAlias();
         else if (verification.getAttributeAlias() != null)
         {
            keyAlias = signature.getAttributes().get(verification.getAttributeAlias());
         }

         if (keyAlias == null)
         {
            result.setFailureReason("Could not find a key alias");
            return result;
         }

         if (verification.getRepository() != null)
         {
            key = verification.getRepository().getPublicKey(keyAlias);
         }
         else if (repository != null)
         {
            key = repository.getPublicKey(keyAlias);
         }
         if (key == null)
         {
            result.setFailureReason("Could not find PublicKey for keyAlias " + keyAlias);
            return result;
         }
      }

      boolean verified = false;
      try
      {
         verified = signature.verify(headers, body, signatures, key, verification.getAlgorithm(), verification.getAttributes());
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
