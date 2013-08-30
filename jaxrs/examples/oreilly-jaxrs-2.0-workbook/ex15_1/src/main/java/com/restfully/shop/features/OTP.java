package com.restfully.shop.features;

import org.jboss.resteasy.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OTP
{
   public static String generateToken(String secret)
   {
      long minutes = System.currentTimeMillis() / 1000 / 60;
      String concat = secret + minutes;
      MessageDigest digest = null;
      try
      {
         digest = MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new IllegalArgumentException(e);
      }
      byte[] hash = digest.digest(concat.getBytes(Charset.forName("UTF-8")));
      return Base64.encodeBytes(hash);
   }
}
