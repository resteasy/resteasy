package org.resteasy.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Encode
{
   public static String encodeSegment(String value)
   {
      try
      {
         return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Keep template parameters and '/' characters intact.
    *
    * @param value
    * @return
    */
   public static String encodePath(String value)
   {
      return encodePath(value, true);
   }

   public static String encodePath(String value, boolean params)
   {
      String[] paths = value.split("/");
      StringBuilder buffer = new StringBuilder();
      boolean first = true;
      for (String path : paths)
      {
         if (first)
         {
            first = false;
         }
         else
         {
            buffer.append("/");
         }
         buffer.append(encodeSegment(path));
      }
      String result = buffer.toString();
      if (value.endsWith("/")) result += "/";
      if (params) result = result.replace("%7B", "{").replace("%7D", "}");
      return result;
   }

}
