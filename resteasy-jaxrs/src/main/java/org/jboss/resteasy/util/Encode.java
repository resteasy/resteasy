package org.jboss.resteasy.util;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

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

   /**
    * decode an encoded map
    *
    * @param map
    * @return
    */
   public static MultivaluedMap<String, String> decode(MultivaluedMap<String, String> map)
   {
      MultivaluedMapImpl<String, String> decoded = new MultivaluedMapImpl<String, String>();
      for (String key : map.keySet())
      {
         List<String> values = map.get(key);
         for (String value : values)
         {
            try
            {
               decoded.add(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      return decoded;
   }

   public static MultivaluedMap<String, String> encode(MultivaluedMap<String, String> map)
   {
      MultivaluedMapImpl<String, String> decoded = new MultivaluedMapImpl<String, String>();
      for (String key : map.keySet())
      {
         List<String> values = map.get(key);
         for (String value : values)
         {
            try
            {
               decoded.add(URLEncoder.encode(key, "UTF-8"), URLEncoder.encode(value, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      return decoded;
   }

}
