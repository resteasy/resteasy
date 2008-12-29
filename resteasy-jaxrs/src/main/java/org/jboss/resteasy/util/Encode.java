package org.jboss.resteasy.util;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Encode
{
   private static final Pattern PARAM_REPLACEMENT = Pattern.compile("_resteasy_uri_parameter");

   /**
    * Keep encoded values "%...", matrix parameters, and '/' characters intact.
    *
    * @param value
    * @return
    * @Param params whether or not to encode stuff between '{' and '}' false means don't encode
    */
   public static String encodePath(String value, boolean ignorePathParams)
   {
      ArrayList<String> params = new ArrayList<String>();
      boolean foundParam = false;
      if (ignorePathParams)
      {
         StringBuffer newPath = new StringBuffer();
         if (savePathParams(value, newPath, params))
         {
            foundParam = true;
            value = newPath.toString();
         }

      }
      String[] segments = value.split("/");
      StringBuilder buffer = new StringBuilder();
      boolean first = true;
      for (String segment : segments)
      {
         if (!first)
         {
            buffer.append("/");
         }
         segment = encodeSegment(segment, ignorePathParams);
         buffer.append(segment);
         first = false;
      }
      String result = buffer.toString();
      if (value.endsWith("/")) result += "/";

      if (ignorePathParams && foundParam)
      {
         result = pathParamReplacement(result, params);
      }

      return result;
   }

   private static final Pattern nonCodes = Pattern.compile("%([^a-fA-F0-7]|$)");

   public static String encodeNonCodes(String string)
   {
      Matcher matcher = nonCodes.matcher(string);
      StringBuffer buf = new StringBuffer();
      while (matcher.find())
      {
         matcher.appendReplacement(buf, "%25$1");
      }
      matcher.appendTail(buf);
      return buf.toString();
   }

   public static String encodeQueryString(String string)
   {
      try
      {
         string = URLEncoder.encode(string, "UTF-8").replace("+", "%20").replace("%25", "%");
         string = encodeNonCodes(string);
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      return string;
   }

   private static boolean savePathParams(String segment, StringBuffer newSegment, List<String> params)
   {
      boolean foundParam = false;
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
      while (matcher.find())
      {
         foundParam = true;
         String group = matcher.group();
         params.add(group);
         matcher.appendReplacement(newSegment, "_resteasy_uri_parameter");
      }
      matcher.appendTail(newSegment);
      return foundParam;
   }

   /**
    * Keep encoded values "%...", matrix parameters, and '/' characters intact.
    *
    * @param segment
    * @return
    * @Param params whether or not to encode stuff between '{' and '}' false means don't encode
    */
   public static String encodeSegment(String segment, boolean ignorePathParams)
   {
      ArrayList<String> params = new ArrayList<String>();
      boolean foundParam = false;
      if (ignorePathParams)
      {
         StringBuffer newSegment = new StringBuffer();
         if (savePathParams(segment, newSegment, params))
         {
            foundParam = true;
            segment = newSegment.toString();
         }
      }
      String result;
      try
      {
         result = URLEncoder.encode(segment, "UTF-8").replace("+", "%20").replace("%3B", ";").replace("%3D", "=").replace("%25", "%");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      segment = result;
      if (ignorePathParams && foundParam)
      {
         segment = pathParamReplacement(segment, params);
      }
      return segment;
   }

   private static String pathParamReplacement(String segment, ArrayList<String> params)
   {
      StringBuffer newSegment = new StringBuffer();
      Matcher matcher = PARAM_REPLACEMENT.matcher(segment);
      int i = 0;
      while (matcher.find())
      {
         String replacement = params.get(i++);
         // double encode slashes, so that slashes stay where they are 
         replacement = replacement.replace("\\", "\\\\"); 
         matcher.appendReplacement(newSegment, replacement);
      }
      matcher.appendTail(newSegment);
      segment = newSegment.toString();
      return segment;
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

   public static String decode(String string)
   {
      try
      {
         return URLDecoder.decode(string, "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void main(String[] args) throws Exception
   {
      System.out.println(encodePath("foo;bar={bar: .*};stuff={  stuff : .*}", true));
   }

}
