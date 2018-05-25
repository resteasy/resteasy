package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.security.SigningAlgorithm;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.ParameterParser;

import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One single signature within a DKIM-Signature header
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DKIMSignature
{
   public static final String DKIM_SIGNATURE = "DKIM-Signature";
   public static final String TIMESTAMP = "t";
   public static final String DOMAIN = "d";
   public static final String EXPIRATION = "x";
   public static final String ALGORITHM = "a";
   public static final String SIGNATURE = "b";
   public static final String HEADERS = "h";
   public static final String IDENTITY = "i";
   public static final String VERSION = "v";
   public static final String BODY_HASH = "bh";
   public static final String CANONICALIZATION = "c";
   public static final String QUERY = "q";
   public static final String SELECTOR = "s";
   public static final String LENGTH = "l";

   /**
    * This is settable
    */
   public static String DEFAULT_SIGNER = "DEFAULT_SIGNER";

   public static final String SHA256WITH_RSA = "SHA256withRSA";

   /**
    * This is settable
    */
   public static String DEFAULT_ALGORITHM = SHA256WITH_RSA;

   protected PrivateKey privateKey;
   protected Map<String, String> attributes = new LinkedHashMap<String, String>();
   protected List<String> headers = new ArrayList<String>();
   protected byte[] signature;
   protected String headerValue;
   protected boolean bodyHashRequired = true;


   public DKIMSignature()
   {
   }

   public DKIMSignature(Map<String, String> attrs)
   {
      attributes = attrs;
      extractAttributes();
   }

   public DKIMSignature(String headerValue)
   {
      this.headerValue = headerValue;
      ParameterParser parser = new ParameterParser();
      attributes = parser.parse(headerValue, ';');
      extractAttributes();
   }

   protected void extractAttributes()
   {
      String heads = attributes.get(HEADERS);
      if (heads != null)
      {
         headers = Arrays.asList(heads.split(":"));
      }
      String sig = attributes.get(SIGNATURE);
      try
      {
         if (sig != null) signature = Base64.decode(sig);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public List<String> getHeaderList()
   {
      return headers;
   }

   /**
    * Generates the Content-Signature value.
    *
    * @return header value
    */
   public String toString()
   {
      return headerValue;
   }

   /**
    * Whether or not to add a body hash to signature.
    *
    * @return body hash required
    */
   public boolean isBodyHashRequired()
   {
      return bodyHashRequired;
   }

   public void setBodyHashRequired(boolean bodyHashRequired)
   {
      this.bodyHashRequired = bodyHashRequired;
   }

   /**
    * Add a reference to a header within the signature calculation.
    *
    * @param headerName header name
    */
   public void addHeader(String headerName)
   {
      headers.add(headerName);
   }

   /**
    * @param name attribute name
    * @param value if null, remove attribute
    */
   public void setAttribute(String name, String value)
   {
      if (value == null)
      {
         attributes.remove(name);
      }
      attributes.put(name, value);
   }

   /**
    * Default value is SHA256withRSA, see Javadoc on java.security.Signature for other supported values.
    *
    * @param value if null, remove attribute
    */
   public void setAlgorithm(String value)
   {
      setAttribute(ALGORITHM, value);
   }

   public void setTimestamp(String value)
   {
      setAttribute(TIMESTAMP, value);
   }

   public void setTimestamp()
   {
      setAttribute(TIMESTAMP, ((new Date()).getTime() / 1000) + "");
   }

   public void setSelector(String selector)
   {
      setAttribute(SELECTOR, selector);
   }

   public String getSelector()
   {
      return attributes.get(SELECTOR);
   }

   public String getQuery()
   {
      return attributes.get(QUERY);
   }

   public void setQuery(String query)
   {
      setAttribute(QUERY, query);
   }

   public void setDomain(String domain)
   {
      setAttribute(DOMAIN, domain);
   }

   public String getDomain()
   {
      return attributes.get(DOMAIN);
   }

   /**
    * @param id id
    */
   public void setId(String id)
   {
      setAttribute(IDENTITY, id);
   }

   public void setExpiration(Date expire)
   {
      setAttribute(EXPIRATION, (expire.getTime() / 1000) + "");
   }

   /**
    * Calculates an expiration date based on the current time plus the additional time units specified in the
    * method parameters.
    *
    * @param seconds number of seconds
    * @param minutes number of minutes
    * @param hours number of hours
    * @param days number of days
    * @param months number of months
    * @param years number of years
    */
   public void setExpiration(int seconds, int minutes, int hours, int days, int months, int years)
   {
      Calendar now = Calendar.getInstance();
      if (seconds > 0) now.add(Calendar.SECOND, seconds);
      if (minutes > 0) now.add(Calendar.MINUTE, minutes);
      if (hours > 0) now.add(Calendar.HOUR, hours);
      if (days > 0) now.add(Calendar.DAY_OF_MONTH, days);
      if (months > 0) now.add(Calendar.MONTH, months);
      if (years > 0) now.add(Calendar.YEAR, years);
      setExpiration(now.getTime());
   }


   /**
    * Return false if true current time.  If expiration isn't set, then just return false. Returns false otherwise.
    *
    * @return true if expired, false otherwise or when expiration attribute is not set
    */
   public boolean isExpired()
   {
      String exp = attributes.get(EXPIRATION);
      if (exp == null) return false;

      long expL = Long.parseLong(exp);
      return (expL * 1000) < (new Date()).getTime();
   }

   /**
    * Returns false if timestamp does not exist or if the current time is
    * greater than timestamp + variables.
    *
    * @param seconds number of seconds
    * @param minutes number of minutes
    * @param hours number of hours
    * @param days number of days
    * @param months number of months
    * @param years number of years
    * 
    * @return true if stale or timestamp attribute is not set
    * 
    */
   public boolean isStale(int seconds, int minutes, int hours, int days, int months, int years)
   {
      String time = attributes.get(TIMESTAMP);
      if (time == null) return true;

      long timeL = Long.parseLong(time);
      Date timestamp = new Date(timeL * 1000L);
      Calendar expires = Calendar.getInstance();
      expires.setTime(timestamp);
      if (seconds > 0) expires.add(Calendar.SECOND, seconds);
      if (minutes > 0) expires.add(Calendar.MINUTE, minutes);
      if (hours > 0) expires.add(Calendar.HOUR, hours);
      if (days > 0) expires.add(Calendar.DAY_OF_MONTH, days);
      if (months > 0) expires.add(Calendar.MONTH, months);
      if (years > 0) expires.add(Calendar.YEAR, years);
      return (new Date()).getTime() > expires.getTime().getTime();
   }

   public String getId()
   {
      return attributes.get(IDENTITY);
   }

   public String getAlgorithm()
   {
      return attributes.get(ALGORITHM);
   }


   public Map<String, String> getAttributes()
   {
      return attributes;
   }

   public String getBased64Signature()
   {
      return attributes.get(SIGNATURE);
   }

   public void setBase64Signature(String signature)
   {
      setAttribute(SIGNATURE, signature);
   }

   public byte[] getSignature()
   {
      return signature;
   }

   public void setSignature(byte[] signature)
   {
      this.signature = signature;
   }

   /**
    * Private key to use to sign the message.  Can be null.  If so, system will try to figure out the signer
    * based on a default value, or the current user principal.
    *
    * @return {@link PrivateKey}
    */
   public PrivateKey getPrivateKey()
   {
      return privateKey;
   }

   public void setPrivateKey(PrivateKey privateKey)
   {
      this.privateKey = privateKey;
   }

   /**
    * Headers can be a {@literal Map<String, Object> or a Map<String, List<Object>>}.  This gives some compatibility with
    * JAX-RS's MultivaluedMap.   If a map of lists, every value of each header duplicate will be added.
    * <p>
    *
    * @param headers headers map
    * @param body if null, bh field will not be set or provided
    * @param defaultKey will be used if privateKey is null
    * @throws SignatureException if security exception occurred
    *
    */
   public void sign(Map headers, byte[] body, PrivateKey defaultKey) throws SignatureException
   {
      PrivateKey key = privateKey == null ? defaultKey : privateKey;
      if (key == null)
      {
         throw new SignatureException(Messages.MESSAGES.privateKeyIsNull());
      }
      attributes.put(VERSION, "1");
      attributes.put(ALGORITHM, SigningAlgorithm.SHA256withRSA.getRfcNotation());
      attributes.put(CANONICALIZATION, "simple/simple");
      String algorithm = SigningAlgorithm.SHA256withRSA.getJavaSecNotation();
      String hashAlgorithm = SigningAlgorithm.SHA256withRSA.getJavaHashNotation();

      Signature signature = null;
      try
      {
         signature = Signature.getInstance(algorithm);
         signature.initSign(key);
      }
      catch (Exception e)
      {
         throw new SignatureException(e);
      }

      if (this.headers.size() > 0)
      {
         StringBuffer headerCat = new StringBuffer();
         int count = 0;
         for (int i = 0; i < this.headers.size(); i++)
         {
            String name = this.headers.get(i);
            if (i > 0) headerCat.append(":");
            headerCat.append(name);
         }
         attributes.put(HEADERS, headerCat.toString());
         updateSignatureWithHeader(headers, signature);
      }

      if (body != null && bodyHashRequired)
      {
         String encodedBodyHash = calculateEncodedHash(body, hashAlgorithm);

         attributes.put(BODY_HASH, encodedBodyHash);
      }

      StringBuffer dosetaBuffer = new StringBuffer();

      boolean first = true;
      for (Map.Entry<String, String> entry : attributes.entrySet())
      {
         if (first) first = false;
         else dosetaBuffer.append(";");

         dosetaBuffer.append(entry.getKey()).append("=").append(entry.getValue());
      }
      if (!first) dosetaBuffer.append(";");
      dosetaBuffer.append("b=");
      String dosetaHeader = dosetaBuffer.toString();
      signature.update(dosetaHeader.getBytes());

      byte[] signed = signature.sign();
      setSignature(signed);
      String base64Signature = Base64.encodeBytes(signed);
      dosetaHeader += base64Signature;
//      System.out.println("***: " + dosetaHeader);
      this.headerValue = dosetaHeader;

   }

   private String calculateEncodedHash(byte[] body, String hashAlgorithm) throws SignatureException
   {
      byte[] bodyHash = hash(body, hashAlgorithm);

      return Base64.encodeBytes(bodyHash);
   }

   private byte[] hash(byte[] body, String hashAlgorithm) throws SignatureException
   {
      MessageDigest digest = null;
      try
      {
         digest = MessageDigest.getInstance(hashAlgorithm);
      }
      catch (Exception e)
      {
         throw new SignatureException(e);
      }

      int length = body.length;

      if (attributes.containsKey(LENGTH))
      {
         length = Integer.parseInt(attributes.get(LENGTH));
      }

      byte[] bodyHash = null;
      digest.update(body, 0, length);
      bodyHash = digest.digest();
      return bodyHash;
   }

   private MultivaluedMap<String, String> updateSignatureWithHeader(Map transmittedHeaders, Signature signature) throws SignatureException
   {
      MultivaluedMap<String, String> verifiedHeaders = new MultivaluedMapImpl<String, String>();
      List<String> list = this.headers;
      Map<String, Integer> count = new HashMap<String, Integer>();
      for (String name : list)
      {
         int index = 0;
         if (count.containsKey(name))
         {
            index = count.get(name);
            index++;
         }
         count.put(name, index);

         Object v = transmittedHeaders.get(name);
         if (v == null)
         {
            throw new SignatureException(Messages.MESSAGES.unableToFindHeader(name, (index > 0 ? "[" + index + "]" : "")));
         }

         if (v instanceof List)
         {
            List l = (List) v;
            int i = l.size() - 1 - index;
            if (i < 0)
            {
               throw new SignatureException(Messages.MESSAGES.unableToFindHeader(name, (index > 0 ? "[" + index + "]" : "")));
            }
            v = l.get(i);
         }
         else if (index > 0)
         {
            throw new SignatureException(Messages.MESSAGES.unableToFindHeader(name, (index > 0 ? "[" + index + "]" : "")));
         }
         String entry = name + ":" + v.toString() + "\r\n";
         signature.update(entry.getBytes());
         verifiedHeaders.add(name, v.toString());
      }
      return verifiedHeaders;
   }

   public MultivaluedMap<String, String> verify(Map headers, byte[] body, PublicKey key) throws SignatureException
   {
      return verify(true, headers, body, key);
   }

   /**
    * Headers can be a {@literal Map<String, Object> or a Map<String, List<Object>>}.  This gives some compatibility with
    * JAX-RS's MultivaluedMap.   If a map of lists, every value of each header duplicate will be added.
    *
    * @param bodyHashRequired body hash required
    * @param headers headers map
    * @param body body
    * @param key public key
    * @return map of verified headers and their values
    * @throws SignatureException signature exception
    */
   public MultivaluedMap<String, String> verify(boolean bodyHashRequired, Map headers, byte[] body, PublicKey key) throws SignatureException
   {
      if (key == null) throw new SignatureException(Messages.MESSAGES.noKeyToVerifyWith());

      String algorithm = getAlgorithm();
      if (algorithm == null || !SigningAlgorithm.SHA256withRSA.getRfcNotation().toLowerCase().equals(algorithm.toLowerCase()))
      {
         throw new SignatureException(Messages.MESSAGES.unsupportedAlgorithm(algorithm));
      }

      Signature verifier = null;
      try
      {
         verifier = Signature.getInstance(SigningAlgorithm.SHA256withRSA.getJavaSecNotation());
         verifier.initVerify(key);
      }
      catch (Exception e)
      {
         throw new SignatureException(e);
      }


      String encodedBh = attributes.get("bh");
      if (encodedBh == null)
      {
         if (body != null && bodyHashRequired) throw new SignatureException(Messages.MESSAGES.thereWasNoBodyHash());
      }
      else
      {

         byte[] bh = hash(body, SigningAlgorithm.SHA256withRSA.getJavaHashNotation());
         byte[] enclosedBh = null;
         try
         {
            enclosedBh = Base64.decode(encodedBh);
         }
         catch (IOException e)
         {
            throw new SignatureException(Messages.MESSAGES.failedToParseBodyHash(), e);
         }

         if (Arrays.equals(bh, enclosedBh) == false)
         {
            throw new SignatureException(Messages.MESSAGES.bodyHashesDoNotMatch());
         }
      }
      MultivaluedMap<String, String> verifiedHeaders = updateSignatureWithHeader(headers, verifier);
      ParameterParser parser = new ParameterParser();
      String strippedHeader = parser.setAttribute(headerValue.toCharArray(), 0, headerValue.length(), ';', "b", "");
      verifier.update(strippedHeader.getBytes());
      if (verifier.verify(getSignature()) == false)
      {
         throw new SignatureException(Messages.MESSAGES.failedToVerifySignature());
      }

      return verifiedHeaders;
   }
}
