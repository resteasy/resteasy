package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.security.keys.KeyRepository;

import java.security.PublicKey;
import java.util.ArrayList;
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

   public boolean verify(ContentSignatures signatures, Map headers, byte[] body)
   {
      for (Verification verification : verifications)
      {
         ContentSignature signature = null;
         if (verification.getId() != null && verification.getId().trim() != "")
         {
            signature = signatures.getBy(ContentSignature.ID, verification.getId());
            if (signature == null)
            {
               verification.setFailureReason("Could not find signature with id: " + verification.getId());
               return false;
            }
         }
         else if (verification.getSigner() != null && verification.getSigner().trim() != "")
         {
            signature = signatures.getBy(ContentSignature.SIGNER, verification.getSigner());
            if (signature == null)
            {
               verification.setFailureReason("Could not find signature with signer: " + verification.getSigner());
               return false;
            }
         }
         else
         {
            if (signatures.getSignatures().size() > 1)
            {
               verification.setFailureReason("Id and Signer are both null for Verification and there are more than one signatures in header");
               return false;
            }
            signature = signatures.getSignatures().get(0);
         }
         boolean success = verifySignature(signatures, headers, body, verification, signature);
         if (success == false)
         {
            return false;
         }

      }
      return true;
   }


   public boolean verifySignature(ContentSignatures signatures, Map headers, byte[] body, Verification verification, ContentSignature signature)
   {
      verification.getSignatures().add(signature);


      PublicKey key = verification.getKey();

      if (key == null)
      {
         String keyAlias = ContentSignature.DEFAULT_SIGNER;
         if (verification.getKeyAlias() != null) keyAlias = verification.getKeyAlias();
         else if (verification.getSigner() != null) keyAlias = verification.getSigner();
         else if (verification.getId() != null) keyAlias = verification.getId();

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
            verification.setFailureReason("Could not find PublicKey for keyAlias " + keyAlias);
            return false;
         }
      }

      boolean verified = false;
      try
      {
         verified = signature.verify(headers, body, signatures, key, verification.getAlgorithm(), verification.getAttributes());
      }
      catch (Exception e)
      {
         verification.setFailureException(e);
         return false;
      }
      if (verified == false)
      {
         verification.setFailureReason("Signature verification failed");
         return false;
      }
      if (verification.isIgnoreExpiration() == false)
      {
         if (signature.isExpired())
         {
            verification.setFailureReason("Signature expired");
            return false;
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
            verification.setFailureReason("Signature is stale");
            return false;
         }
      }
      verification.setVerified(true);
      return true;
   }

}
