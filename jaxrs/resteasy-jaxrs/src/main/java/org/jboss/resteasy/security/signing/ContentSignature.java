package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.Hex;
import org.jboss.resteasy.util.ParameterParser;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One single signature within a Content-Signature header
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContentSignature
{
   public static final String CONTENT_SIGNATURE = "Content-Signature";
   public static final String TIMESTAMP = "timestamp";
   public static final String SIGNER = "signer";
   public static final String EXPIRATION = "expiration";
   public static final String ALGORITHM = "algorithm";
   public static final String SIGNATURE = "signature";
   public static final String SIGNATURE_REFS = "signature-refs";
   public static final String VALUES = "values";
   public static final String HEADERS = "headers";
   public static final String ID = "id";

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
   protected Map<String, String> attributes = new HashMap<String, String>();
   protected List<String> displayedAttributes = new ArrayList<String>();
   protected List<String> values = new ArrayList<String>();
   protected List<String> headers = new ArrayList<String>();
   protected List<String> signatureRefs = new ArrayList<String>();
   protected String keyAlias;
   protected byte[] signature;


   public ContentSignature()
   {
   }

   public ContentSignature(Map<String, String> attrs)
   {
      attributes = attrs;
      extractAttributes();
   }

   public ContentSignature(String headerValue)
   {
      ParameterParser parser = new ParameterParser();
      parser.setLowerCaseNames(true);
      attributes = parser.parse(headerValue, ';');
      extractAttributes();
   }

   protected void extractAttributes()
   {
      String vals = attributes.get(VALUES);
      if (vals != null)
      {
         values = Arrays.asList(vals.split(":"));
      }
      String heads = attributes.get(HEADERS);
      if (heads != null)
      {
         headers = Arrays.asList(heads.split(":"));
      }
      String refs = attributes.get(SIGNATURE_REFS);
      if (refs != null)
      {
         signatureRefs = Arrays.asList(refs.split(":"));
      }
      String sig = attributes.get(SIGNATURE);
      if (sig != null) signature = Hex.decodeHex(sig);
   }

   /**
    * Generates the Content-Signature value.
    *
    * @return
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      boolean firstAttr = true;
      if (values.size() > 0)
      {
         if (!firstAttr) buf.append(";");
         else firstAttr = false;
         buf.append("values=");
         boolean first = true;
         for (String val : values)
         {
            if (first) first = false;
            else buf.append(":");
            buf.append(val);
         }
      }
      if (headers.size() > 0)
      {
         if (!firstAttr) buf.append(";");
         else firstAttr = false;
         buf.append("headers=");
         boolean first = true;
         for (String header : headers)
         {
            if (first) first = false;
            else buf.append(":");
            buf.append(header);
         }
      }
      if (signatureRefs.size() > 0)
      {
         if (!firstAttr) buf.append(";");
         else firstAttr = false;
         buf.append("signature-refs=");
         boolean first = true;
         for (String header : signatureRefs)
         {
            if (first) first = false;
            else buf.append(":");
            buf.append(header);
         }
      }
      for (String name : displayedAttributes)
      {
         String val = attributes.get(name);
         if (!firstAttr) buf.append(";");
         else firstAttr = false;
         buf.append(name).append("=");
         boolean needsQuotes = val.indexOf(',') > -1 || val.indexOf(';') > -1;
         if (needsQuotes) buf.append("\"");
         buf.append(val);
         if (needsQuotes) buf.append("\"");
      }
      if (signature != null)
      {
         if (!firstAttr) buf.append(";");
         else firstAttr = false;
         buf.append("signature=").append(Hex.encodeHex(signature));
      }
      return buf.toString();
   }

   /**
    * alias to use when looking for a key within a KeyRepository
    *
    * @return
    */
   public String getKeyAlias()
   {
      return keyAlias;
   }

   public void setKeyAlias(String keyAlias)
   {
      this.keyAlias = keyAlias;
   }

   /**
    * Add a signature reference to the signature.  This corresponds to the signature-ref attribute defined by the
    * Content-Signature protocol.
    *
    * @param signatures
    * @param id         must exist in signatures paramemter
    */
   public void addSignatureRef(ContentSignatures signatures, String id)
   {
      ContentSignature sig = signatures.getFirstBy(ID, id);
      if (sig == null)
      {
         throw new RuntimeException("ContentSignatures does not contain id, " + id);
      }
      signatureRefs.add(id);
   }

   /**
    * Add a reference to a header within the signature calculation
    *
    * @param headerName
    */
   public void addHeader(String headerName)
   {
      headers.add(headerName);
   }

   /**
    *
    * @param name
    * @param value
    * @param includeSignature true if you want attribute to be included within the signature calculation
    * @param display true if you want attribute shown in the Content-Signature
    */
   public void setAttribute(String name, String value, boolean includeSignature, boolean display)
   {
      attributes.put(name, value);
      if (includeSignature) values.add(name);
      if (display) displayedAttributes.add(name);
   }

   /**
    * Default value is SHA256withRSA, see Javadoc on java.security.Signature for other supported values.
    *
    * @param value
    * @param includeSignature true if you want attribute to be included within the signature calculation
    * @param display true if you want attribute shown in the Content-Signature
    */
   public void setAlgorithm(String value, boolean includeSignature, boolean display)
   {
      setAttribute(ALGORITHM, value, includeSignature, display);
   }

   public void setTimestamp(String value)
   {
      setAttribute(TIMESTAMP, value, true, true);
   }

   public void setTimestamp()
   {
      setAttribute(TIMESTAMP, DateUtil.formatDate(new Date()), true, true);
   }


   /**
    * @param value
    * @param includeSignature true if you want attribute to be included within the signature calculation
    * @param display true if you want attribute shown in the Content-Signature
    */
   public void setSigner(String signer, boolean includeSignature, boolean display)
   {
      setAttribute(SIGNER, signer, includeSignature, display);
   }

   /**
    * Default value is SHA256withRSA, see Javadoc on java.security.Signature for other supported values.
    *
    * @param value
    * @param includeSignature true if you want attribute to be included within the signature calculation
    */
   public void setId(String id, boolean includeSignature)
   {
      setAttribute(ID, id, includeSignature, true);
   }

   public void setExpiration(Date expire)
   {
      setAttribute(EXPIRATION, DateUtil.formatDate(expire), true, true);
   }

   /**
    * Calculates an expiration date based on the current time plus the additional time units specified in the
    * method parameters.
    *
    * @param seconds
    * @param minutes
    * @param hours
    * @param days
    * @param months
    * @param years
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
    * Tests whether expiration attribute exists.  If not, return false, if available, check against current time.
    *
    * @return
    */
   public boolean isExpired()
   {
      String exp = attributes.get(EXPIRATION);
      if (exp == null) return false;

      Date expiration = DateUtil.parseDate(exp);
      return expiration.getTime() < (new Date()).getTime();
   }

   /**
    * Tests whether the timestamp attribute exists.  If not, return false, if available check if the current time is
    * greater than timestamp + variables
    */
   public boolean isStale(int seconds, int minutes, int hours, int days, int months, int years)
   {
      String time = attributes.get(TIMESTAMP);
      if (time == null) return false;

      Date timestamp = DateUtil.parseDate(time);
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

   /**
    * id attribute of the Content-Signature
    *
    * @return
    */
   public String getId()
   {
      return attributes.get(ID);
   }

   public String getSigner()
   {
      return attributes.get(SIGNER);
   }

   public String getAlgorithm()
   {
      return attributes.get(ALGORITHM);
   }


   public Map<String, String> getAttributes()
   {
      return attributes;
   }

   public String getHexSignature()
   {
      return attributes.get(SIGNATURE);
   }

   public void setHexSignature(String signature)
   {
      setAttribute(SIGNATURE, signature, false, true);
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
    * based on a default value, or the current user principal
    *
    * @return
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
    * Headers can be a Map<String, Object> or a Map<String, List<Object>>.  This gives some compatibility with
    * JAX-RS's MultivaluedMap.   If a map of lists, every value of each header duplicate will be added.
    * <p/>
    * setPrivateKey() must be set before calling this method
    *
    * @param headers
    * @param body
    * @param defaultKey will be used if privateKey is null
    * @throws GeneralSecurityException
    */
   public void sign(Map headers, byte[] body, ContentSignatures parent, PrivateKey defaultKey) throws SignatureException
   {
      PrivateKey key = privateKey == null ? defaultKey : privateKey;
      if (key == null)
      {
         throw new SignatureException("private key is null, cannot sign");
      }
      String algorithm = getAlgorithm();
      if (algorithm == null) algorithm = ContentSignature.DEFAULT_ALGORITHM;
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

      for (String val : values)
      {
         String attr = getAttributes().get(val);
         if (attr == null)
         {
            throw new SignatureException("Unable to find attribute " + attr + " to sign header");
         }
         //System.out.println("adding to signature " + val + "=" + attr);
         signature.update(attr.getBytes());
      }
      for (String name : this.headers)
      {
         updateSignatureWithHeader(headers, signature, name);
      }
      for (String id : signatureRefs)
      {
         if (parent == null)
         {
            throw new SignatureException("ContentSignatures was null so could not look up signature-ref: " + id);
         }
         ContentSignature ref = parent.getFirstBy(ID, id);
         if (ref == null)
         {
            throw new SignatureException("Could not find ContentSignature with id " + id + " to add as signature-ref");
         }
         String hexSigRef = ref.getHexSignature();
         if (hexSigRef == null)
         {
            throw new SignatureException("Referenced signature " + id + " is not signed.  Order your signatures correctly.");
         }
         hexSigRef = ref.getHexSignature();
         signature.update(hexSigRef.getBytes());

      }
      signature.update(body);
      byte[] signed = signature.sign();
      setSignature(signed);

   }

   private void updateSignatureWithHeader(Map headers, Signature signature, String name) throws SignatureException
   {
      Object h = headers.get(name);
      if (h == null)
      {
         throw new SignatureException("Unable to find header " + name + " to sign header with");
      }
      if (h instanceof List)
      {
         List l = (List) h;
         for (Object obj : l)
         {
            signature.update(obj.toString().getBytes());
         }
      }
      else
      {
         signature.update(h.toString().getBytes());
      }
   }


   public boolean verify(Map headers, byte[] body, PublicKey key) throws SignatureException
   {
      return verify(headers, body, null, key, null, null);
   }

   /**
    * Headers can be a Map<String, Object> or a Map<String, List<Object>>.  This gives some compatibility with
    * JAX-RS's MultivaluedMap.   If a map of lists, every value of each header duplicate will be added.
    *
    * @param headers
    * @param body
    * @param parent            can be null
    * @param key
    * @param defaultAlgorithm  can be null
    * @param defaultAttributes can be null
    * @return
    * @throws SignatureException
    */
   public boolean verify(Map headers, byte[] body, ContentSignatures parent, PublicKey key, String defaultAlgorithm, Map<String, String> defaultAttributes) throws SignatureException
   {
      String algorithm = getAlgorithm();
      if (algorithm == null)
      {
         algorithm = defaultAlgorithm == null ? DEFAULT_ALGORITHM : defaultAlgorithm;
      }

      Signature verifier = null;
      try
      {
         verifier = Signature.getInstance(algorithm);
         verifier.initVerify(key);
      }
      catch (Exception e)
      {
         throw new SignatureException(e);
      }
      for (String name : this.values)
      {
         String val = getAttributes().get(name);
         if (val == null && defaultAttributes != null)
         {
            val = defaultAttributes.get(name);
         }
         if (val == null)
         {
            throw new SignatureException("Could not find attribute value for " + name + " within signature.");
         }
         verifier.update(val.getBytes());
      }
      for (String name : this.headers)
      {
         updateSignatureWithHeader(headers, verifier, name);
      }
      for (String id : this.signatureRefs)
      {
         if (parent == null)
         {
            throw new SignatureException("ContentSignatures was null so could not look up signature-ref: " + id);
         }
         ContentSignature ref = parent.getFirstBy(ID, id);
         if (ref == null)
         {
            throw new SignatureException("Could not find ContentSignature with id " + id + " to add as signature-ref");
         }
         String hexSigRef = ref.getHexSignature();
         if (hexSigRef == null)  // todo possible circular reference
         {
            throw new SignatureException("Signature attribute was null for signature-ref " + id);
         }
         hexSigRef = ref.getHexSignature();
         verifier.update(hexSigRef.getBytes());

      }
      verifier.update(body);
      return verifier.verify(getSignature());
   }
}
