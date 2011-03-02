package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.security.keys.KeyRepository;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Verification
{
   protected PublicKey key;
   protected KeyRepository repository;
   protected String algorithm = ContentSignature.DEFAULT_ALGORITHM;
   protected Map<String, String> attributes = new HashMap<String, String>();
   protected String signer;
   protected String keyAlias;
   protected String id;
   protected boolean staleCheck;
   protected boolean ignoreExpiration;
   protected int staleSeconds;
   protected int staleMinutes;
   protected int staleHours;
   protected int staleDays;
   protected int staleMonths;
   protected int staleYears;
   protected boolean verified;
   protected String failureReason;
   protected Exception failureException;
   protected List<ContentSignature> signatures = new ArrayList<ContentSignature>();


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

   public String getKeyAlias()
   {
      return keyAlias;
   }

   public void setKeyAlias(String keyAlias)
   {
      this.keyAlias = keyAlias;
   }

   public List<ContentSignature> getSignatures()
   {
      return signatures;
   }

   public boolean isVerified()
   {
      return verified;
   }

   public void setVerified(boolean verified)
   {
      this.verified = verified;
   }

   public String getFailureReason()
   {
      return failureReason;
   }

   public void setFailureReason(String failureReason)
   {
      this.failureReason = failureReason;
   }

   public Exception getFailureException()
   {
      return failureException;
   }

   public void setFailureException(Exception failureException)
   {
      this.failureException = failureException;
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
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

   public Map<String, String> getAttributes()
   {
      return attributes;
   }

   public String getSigner()
   {
      return signer;
   }

   public void setSigner(String signer)
   {
      this.signer = signer;
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
}
