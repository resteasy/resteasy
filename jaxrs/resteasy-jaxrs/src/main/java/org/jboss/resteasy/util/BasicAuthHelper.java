package org.jboss.resteasy.util;

import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthHelper
{
   public static String createHeader(String username, String password)
   {
      StringBuffer buf = new StringBuffer(username);
      buf.append(':').append(password);
      try
      {
         return "Basic " + Base64.encodeBytes(buf.toString().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static String[] parseHeader(String header)
   {
      if (header.length() < 6) return null;
      String type = header.substring(0, 5);
      type = type.toLowerCase();
      if (!type.equalsIgnoreCase("Basic")) return null;
      String val = header.substring(6);
      val = new String(org.apache.commons.codec.binary.Base64.decodeBase64(val.getBytes()));
      String[] split = val.split(":");
      if (split.length != 2) return null;
      return split;
   }
}
