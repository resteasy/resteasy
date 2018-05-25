package org.jboss.resteasy.security.doseta;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.security.doseta.i18n.Messages;

import java.security.PublicKey;
import java.security.SignatureException;
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

   /**
    * Try to verify a set of signatures and store the results.
    *
    * @param signatures list of signatures
    * @param headers headers map
    * @param body body
    * @return {@link VerificationResults}
    */
   public VerificationResults verify(List<DKIMSignature> signatures, Map headers, byte[] body)
   {
      VerificationResults results = new VerificationResults();
      results.setVerified(true);
      for (Verification verification : verifications)
      {
         VerificationResultSet resultSet = new VerificationResultSet();
         results.getResults().add(resultSet);
         resultSet.setVerification(verification);

         List<DKIMSignature> matched = new ArrayList<DKIMSignature>();
         matched.addAll(signatures);
         Iterator<DKIMSignature> iterator = matched.iterator();


         while (iterator.hasNext())
         {
            DKIMSignature sig = iterator.next();
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
         for (DKIMSignature signature : matched)
         {
            VerificationResult result = verify(headers, body, verification, signature);
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

   /**
    * Verify one signature and store the results.
    *
    * @param headers headers map
    * @param body body
    * @param verification verification
    * @param signature signature
    * @return {@link VerificationResult}
    */
   public VerificationResult verify(Map headers, byte[] body, Verification verification, DKIMSignature signature)
   {
      VerificationResult result = new VerificationResult();
      result.setSignature(signature);
      try
      {
         MultivaluedMap<String, String> verifiedHeaders = verifySignature(headers, body, verification, signature);
         result.setVerifiedHeaders(verifiedHeaders);
      }
      catch (Exception e)
      {
         result.setFailureException(e);
         return result;
      }
      result.setVerified(true);
      return result;
   }

   public MultivaluedMap<String, String> verifySignature(Map headers, byte[] body, Verification verification, DKIMSignature signature) throws SignatureException
   {
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
            throw new SignatureException(Messages.MESSAGES.couldNotFindPublicKey(signature));
         }
      }

      return verification.verify(signature, headers, body, key);
   }


}
