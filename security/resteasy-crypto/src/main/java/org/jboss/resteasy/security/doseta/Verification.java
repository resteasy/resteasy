package org.jboss.resteasy.security.doseta;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.security.doseta.i18n.Messages;

import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Verification
{
   protected PublicKey key;
   protected KeyRepository repository;
   protected String algorithm = DKIMSignature.DEFAULT_ALGORITHM;
   protected Map<String, String> requiredAttributes = new HashMap<String, String>();
   protected String identifierName;
   protected String identifierValue;
   protected boolean staleCheck;
   protected boolean ignoreExpiration;
   protected int staleSeconds;
   protected int staleMinutes;
   protected int staleHours;
   protected int staleDays;
   protected int staleMonths;
   protected int staleYears;
   protected boolean bodyHashRequired = true;


   public Verification()
   {
   }

   public Verification(PublicKey key)
   {
      this.key = key;
   }

   public Verification(KeyRepository repository)
   {
      this.repository = repository;
   }

   public boolean isBodyHashRequired()
   {
      return bodyHashRequired;
   }

   public void setBodyHashRequired(boolean bodyHashRequired)
   {
      this.bodyHashRequired = bodyHashRequired;
   }

   public String getIdentifierName()
   {
      return identifierName;
   }

   public void setIdentifierName(String identifierName)
   {
      this.identifierName = identifierName;
   }

   public String getIdentifierValue()
   {
      return identifierValue;
   }

   public void setIdentifierValue(String identifierValue)
   {
      this.identifierValue = identifierValue;
   }

   public boolean isIgnoreExpiration()
   {
      return ignoreExpiration;
   }

   public void setIgnoreExpiration(boolean ignoreExpiration)
   {
      this.ignoreExpiration = ignoreExpiration;
   }

   public boolean isStaleCheck()
   {
      return staleCheck;
   }

   public void setStaleCheck(boolean staleCheck)
   {
      this.staleCheck = staleCheck;
   }

   public Map<String, String> getRequiredAttributes()
   {
      return requiredAttributes;
   }

   public String getAlgorithm()
   {
      return algorithm;
   }

   public void setAlgorithm(String algorithm)
   {
      this.algorithm = algorithm;
   }

   public PublicKey getKey()
   {
      return key;
   }

   public void setKey(PublicKey key)
   {
      this.key = key;
   }

   public KeyRepository getRepository()
   {
      return repository;
   }

   public void setRepository(KeyRepository repository)
   {
      this.repository = repository;
   }

   public int getStaleSeconds()
   {
      return staleSeconds;
   }

   public void setStaleSeconds(int staleSeconds)
   {
      this.staleSeconds = staleSeconds;
   }

   public int getStaleMinutes()
   {
      return staleMinutes;
   }

   public void setStaleMinutes(int staleMinutes)
   {
      this.staleMinutes = staleMinutes;
   }

   public int getStaleHours()
   {
      return staleHours;
   }

   public void setStaleHours(int staleHours)
   {
      this.staleHours = staleHours;
   }

   public int getStaleDays()
   {
      return staleDays;
   }

   public void setStaleDays(int staleDays)
   {
      this.staleDays = staleDays;
   }

   public int getStaleMonths()
   {
      return staleMonths;
   }

   public void setStaleMonths(int staleMonths)
   {
      this.staleMonths = staleMonths;
   }

   public int getStaleYears()
   {
      return staleYears;
   }

   public void setStaleYears(int staleYears)
   {
      this.staleYears = staleYears;
   }

   /**
    * Headers can be a {@literal Map<String, Object> or a Map<String, List<Object>>}.  This gives some compatibility with
    * JAX-RS's MultivaluedMap.   If a map of lists, every value of each header duplicate will be added.

    *
    * @param signature signature
    * @param headers headers map
    * @param body body
    * @param publicKey public key
    * @return map of validated headers and their values
    * @throws SignatureException if verification fails
    */
   public MultivaluedMap<String, String> verify(DKIMSignature signature, Map headers, byte[] body, PublicKey publicKey) throws SignatureException
   {
      if (publicKey == null) publicKey = key;
      if (publicKey == null) throw new SignatureException(Messages.MESSAGES.publicKeyIsNull());
      
      MultivaluedMap<String, String> verifiedHeaders = signature.verify(bodyHashRequired, headers, body, publicKey);

      if (isIgnoreExpiration() == false)
      {
         if (signature.isExpired())
         {
            throw new SignatureException(Messages.MESSAGES.signatureExpired());
         }
      }
      if (isStaleCheck())
      {
         if (signature.isStale(getStaleSeconds(),
                 getStaleMinutes(),
                 getStaleHours(),
                 getStaleDays(),
                 getStaleMonths(),
                 getStaleYears()))
         {
            throw new SignatureException(Messages.MESSAGES.signatureIsStale());
         }
      }

      for (Map.Entry<String, String> required : getRequiredAttributes().entrySet())
      {
         String value = signature.getAttributes().get(required.getKey());
         if (!value.equals(required.getValue()))
         {
            throw new SignatureException(Messages.MESSAGES.expectedValue(required.getValue(), value, required.getKey()));
         }
      }

      return verifiedHeaders;

   }
}
