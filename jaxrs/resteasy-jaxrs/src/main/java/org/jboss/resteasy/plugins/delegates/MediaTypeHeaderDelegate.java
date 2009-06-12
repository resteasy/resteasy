package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHeaderDelegate implements RuntimeDelegate.HeaderDelegate
{
   public Object fromString(String type) throws IllegalArgumentException
   {
      if (type == null) throw new IllegalArgumentException("MediaType value is null");
      return parse(type);
   }

   private static int getEndName(String params, int start)
   {
      int equals = params.indexOf('=', start);
      int semicolon = params.indexOf(';', start);
      if (equals == -1 && semicolon == -1) return params.length();
      if (equals == -1) return semicolon;
      if (semicolon == -1) return equals;
      int end = (equals < semicolon) ? equals : semicolon;
      return end;
   }

   public static int setParam(HashMap<String, String> typeParams, String params, int start)
   {
      boolean quote = false;
      boolean backslash = false;

      int end = getEndName(params, start);
      String name = params.substring(start, end).trim();
      if (end < params.length() && params.charAt(end) == '=') end++;

      StringBuffer buffer = new StringBuffer();
      int i = end;
      for (; i < params.length(); i++)
      {
         char c = params.charAt(i);

         switch (c)
         {
            case '"':
            {
               if (backslash)
               {
                  backslash = false;
                  buffer.append(c);
               }
               else
               {
                  quote = !quote;
               }
               break;
            }
            case '\\':
            {
               if (backslash)
               {
                  backslash = false;
                  buffer.append(c);
               }
               break;
            }
            case ';':
            {
               if (!quote)
               {
                  String value = buffer.toString().trim();
                  typeParams.put(name, value);
                  return i + 1;
               }
               else
               {
                  buffer.append(c);
               }
               break;
            }
            default:
            {
               buffer.append(c);
               break;
            }
         }
      }
      String value = buffer.toString().trim();
      typeParams.put(name, value);
      return i;
   }


   public static MediaType parse(String type)
   {
      String params = null;
      int idx = type.indexOf(";");
      if (idx > -1)
      {
         params = type.substring(idx + 1).trim();
         type = type.substring(0, idx);
      }
      String major = null;
      String subtype = null;
      String[] paths = type.split("/");
      if (paths.length < 2 && type.equals("*"))
      {
         major = "*";
         subtype = "*";

      }
      else if (paths.length != 2)
      {
         throw new IllegalArgumentException("Failure parsing MediaType string: " + type);
      }
      else if (paths.length == 2)
      {
         major = paths[0];
         subtype = paths[1];
      }
      if (params != null && !params.equals(""))
      {
         HashMap<String, String> typeParams = new HashMap<String, String>();

         int start = 0;

         while (start < params.length())
         {
            start = setParam(typeParams, params, start);
         }
         return new MediaType(major, subtype, typeParams);
      }
      else
      {
         return new MediaType(major, subtype);
      }
   }

   public String toString(Object o)
   {
      MediaType type = (MediaType) o;
      String rtn = type.getType().toLowerCase() + "/" + type.getSubtype().toLowerCase();
      if (type.getParameters() == null || type.getParameters().size() == 0) return rtn;
      for (String name : type.getParameters().keySet())
      {
         String val = type.getParameters().get(name);
         rtn += ";" + name + "=\"" + val + "\"";
      }
      return rtn;
   }
}
