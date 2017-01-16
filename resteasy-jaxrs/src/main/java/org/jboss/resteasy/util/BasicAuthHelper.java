package org.jboss.resteasy.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


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
      return "Basic " + Base64.encodeBytes(buf.toString().getBytes(StandardCharsets.UTF_8));
   }

   public static String[] parseHeader(String header)
   {
      if (header.length() < 6) return null;
      String type = header.substring(0, 5);
      type = type.toLowerCase();
      if (!type.equalsIgnoreCase("Basic")) return null;
      String val = header.substring(6);
      val = new String(org.apache.commons.codec.binary.Base64.decodeBase64(val.getBytes()));
      int pos = val.indexOf(':');
      String[] split = new String[2];
      split[0] = val.substring(0, pos);
      split[1] = val.substring(pos + 1);
      return split;
   }
}
