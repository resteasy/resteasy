package org.jboss.resteasy.util;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Encode
{
   private static final Pattern PARAM_REPLACEMENT = Pattern.compile("_resteasy_uri_parameter");

   private static final String[] pathEncoding = new String[128];
   private static final String[] pathSegmentEncoding = new String[128];
   private static final String[] matrixParameterEncoding = new String[128];
   private static final String[] queryNameValueEncoding = new String[128];
   private static final String[] queryStringEncoding = new String[128];

   static
   {
      /*
       * Encode via <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986</a>.  PCHAR is allowed allong with '/'
       *
       * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
       * sub-delims  = "!" / "$" / "&" / "'" / "(" / ")"
                     / "*" / "+" / "," / ";" / "="
       * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
       *
       */
      for (int i = 0; i < 128; i++)
      {
         if (i >= 'a' && i <= 'z') continue;
         if (i >= 'A' && i <= 'Z') continue;
         if (i >= '0' && i <= '9') continue;
         switch ((char) i)
         {
            case '-':
            case '.':
            case '_':
            case '~':
            case '!':
            case '$':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '/':
            case ';':
            case '=':
            case ':':
            case '@':
               continue;
         }
         StringBuffer sb = new StringBuffer();
         sb.append((char) i);
         pathEncoding[i] = URLEncoder.encode(sb.toString());
      }
      pathEncoding[' '] = "%20";
      System.arraycopy(pathEncoding, 0, matrixParameterEncoding, 0, pathEncoding.length);
      matrixParameterEncoding[';'] = "%3B";
      matrixParameterEncoding['='] = "%3D";
      System.arraycopy(pathEncoding, 0, pathSegmentEncoding, 0, pathEncoding.length);
      pathSegmentEncoding['/'] = "%2F";
      /*
       * Encode via <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986</a>.
       *
       * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
       * space encoded as '+'
       *
       */
      for (int i = 0; i < 128; i++)
      {
         if (i >= 'a' && i <= 'z') continue;
         if (i >= 'A' && i <= 'Z') continue;
         if (i >= '0' && i <= '9') continue;
         switch ((char) i)
         {
            case '-':
            case '.':
            case '_':
            case '~':
            case '?':
               continue;
            case ' ':
               queryNameValueEncoding[i] = "+";
               continue;
         }
         StringBuffer sb = new StringBuffer();
         sb.append((char) i);
         queryNameValueEncoding[i] = URLEncoder.encode(sb.toString());
      }

      /*
       * query       = *( pchar / "/" / "?" )

       */
      for (int i = 0; i < 128; i++)
      {
         if (i >= 'a' && i <= 'z') continue;
         if (i >= 'A' && i <= 'Z') continue;
         if (i >= '0' && i <= '9') continue;
         switch ((char) i)
         {
            case '-':
            case '.':
            case '_':
            case '~':
            case '!':
            case '$':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case ';':
            case '=':
            case ':':
            case '@':
            case '?':
            case '/':
               continue;
            case ' ':
               queryStringEncoding[i] = "%20";
               continue;
         }
         StringBuffer sb = new StringBuffer();
         sb.append((char) i);
         queryStringEncoding[i] = URLEncoder.encode(sb.toString());
      }
   }

   /**
    * Keep encoded values "%..." and template parameters intact.
    */
   public static String encodeQueryString(String value)
   {
      return encodeValue(value, queryStringEncoding);
   }

   /**
    * Keep encoded values "%...", matrix parameters, template parameters, and '/' characters intact.
    */
   public static String encodePath(String value)
   {
      return encodeValue(value, pathEncoding);
   }

   /**
    * Keep encoded values "%...", matrix parameters and template parameters intact.
    */
   public static String encodePathSegment(String value)
   {
      return encodeValue(value, pathSegmentEncoding);
   }

   /**
    * Keep encoded values "%..." and template parameters intact.
    */
   public static String encodeFragment(String value)
   {
      return encodeValue(value, queryNameValueEncoding);
   }

   /**
    * Keep encoded values "%..." and template parameters intact.
    */
   public static String encodeMatrixParam(String value)
   {
      return encodeValue(value, matrixParameterEncoding);
   }

   /**
    * Keep encoded values "%..." and template parameters intact.
    */
   public static String encodeQueryParam(String value)
   {
      return encodeValue(value, queryNameValueEncoding);
   }

   private static final Pattern nonCodes = Pattern.compile("%([^a-fA-F0-9]|$)");
   private static final Pattern encodedChars = Pattern.compile("%([a-fA-F0-9][a-fA-F0-9])");

   public static String decodePath(String path)
   {
      Matcher matcher = encodedChars.matcher(path);
      StringBuffer buf = new StringBuffer();
      while (matcher.find())
      {
         String enc = matcher.group(1);
         int c = Integer.parseInt(enc, 16);
         StringBuffer cBuf = new StringBuffer();
         cBuf.append((char) c);
         matcher.appendReplacement(buf, cBuf.toString());
      }
      matcher.appendTail(buf);
      return buf.toString();
   }

   /**
    * Encode '%' if it is not an encoding sequence
    *
    * @param string
    * @return
    */
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

   private static boolean savePathParams(String segment, StringBuffer newSegment, List<String> params)
   {
      boolean foundParam = false;
      // Regular expressions can have '{' and '}' characters.  Replace them to do match
      segment = PathHelper.replaceEnclosedCurlyBraces(segment);
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
      while (matcher.find())
      {
         foundParam = true;
         String group = matcher.group();
         // Regular expressions can have '{' and '}' characters.  Recover earlier replacement
         params.add(PathHelper.recoverEnclosedCurlyBraces(group));
         matcher.appendReplacement(newSegment, "_resteasy_uri_parameter");
      }
      matcher.appendTail(newSegment);
      return foundParam;
   }

   /**
    * Keep encoded values "%..." and template parameters intact i.e. "{x}"
    *
    * @param segment
    * @param encoding
    * @return
    */
   public static String encodeValue(String segment, String[] encoding)
   {
      ArrayList<String> params = new ArrayList<String>();
      boolean foundParam = false;
      StringBuffer newSegment = new StringBuffer();
      if (savePathParams(segment, newSegment, params))
      {
         foundParam = true;
         segment = newSegment.toString();
      }
      String result = encodeFromArray(segment, encoding, false);
      result = encodeNonCodes(result);
      segment = result;
      if (foundParam)
      {
         segment = pathParamReplacement(segment, params);
      }
      return segment;
   }

   /**
    * Encode via <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986</a>.  PCHAR is allowed allong with '/'
    * <p/>
    * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
    * sub-delims  = "!" / "$" / "&" / "'" / "(" / ")"
    * / "*" / "+" / "," / ";" / "="
    * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
    */
   public static String encodePathAsIs(String segment)
   {
      return encodeFromArray(segment, pathEncoding, true);
   }

   /**
    * Keep any valid encodings from string i.e. keep "%2D" but don't keep "%p"
    *
    * @param segment
    * @return
    */
   public static String encodePathSaveEncodings(String segment)
   {
      String result = encodeFromArray(segment, pathEncoding, false);
      result = encodeNonCodes(result);
      return result;
   }

   /**
    * Encodes everything of a query parameter name or value.
    *
    * @param nameOrValue
    * @return
    */
   public static String encodeQueryParamAsIs(String nameOrValue)
   {
      return encodeFromArray(nameOrValue, queryNameValueEncoding, true);
   }

   /**
    * Keep any valid encodings from string i.e. keep "%2D" but don't keep "%p"
    *
    * @param segment
    * @return
    */
   public static String encodeQueryParamSaveEncodings(String segment)
   {
      String result = encodeFromArray(segment, queryNameValueEncoding, false);
      result = encodeNonCodes(result);
      return result;
   }

   public static String encodeFragmentAsIs(String nameOrValue)
   {
      return encodeFromArray(nameOrValue, queryNameValueEncoding, true);
   }

   protected static String encodeFromArray(String segment, String[] encodingMap, boolean encodePercent)
   {
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < segment.length(); i++)
      {
         if (!encodePercent && segment.charAt(i) == '%')
         {
            result.append(segment.charAt(i));
            continue;
         }
         int idx = segment.charAt(i);
         String encoding = encodingMap[idx];
         if (encoding == null)
         {
            result.append(segment.charAt(i));
         }
         else
         {
            result.append(encoding);
         }
      }
      return result.toString();
   }

   private static String pathParamReplacement(String segment, List<String> params)
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
      for (Map.Entry<String, List<String>> entry : map.entrySet())
      {
         List<String> values = entry.getValue();
         for (String value : values)
         {
            try
            {
               decoded.add(URLDecoder.decode(entry.getKey(), "UTF-8"), URLDecoder.decode(value, "UTF-8"));
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
      for (Map.Entry<String, List<String>> entry : map.entrySet())
      {
         List<String> values = entry.getValue();
         for (String value : values)
         {
            try
            {
               decoded.add(URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(value, "UTF-8"));
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

}
