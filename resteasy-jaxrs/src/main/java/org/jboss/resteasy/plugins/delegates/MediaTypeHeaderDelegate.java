package org.jboss.resteasy.plugins.delegates;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.HeaderParameterParser;

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
      if (type == null) throw new IllegalArgumentException(Messages.MESSAGES.mediaTypeValueNull());
      return parse(type);
   }

   /*
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
      else if (paths.length != 2
            || "".equals(paths[0]) || "".equals(paths[1])
            || paths[0].contains(" ") || paths[1].contains(" "))
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
            start = HeaderParameterParser.setParam(typeParams, params, start);
         }
         return new MediaType(major, subtype, typeParams);
      }
      else
      {
         return new MediaType(major, subtype);
      }
   }
   */

   protected static boolean isValid(String str)
   {
      if (str == null || str.length() == 0) return false;
      for (int i = 0; i < str.length(); i++) {
         switch (str.charAt(i))
         {
            case '/':
            case '\\':
            case '?':
            case ':':
            case '<':
            case '>':
            case ';':
            case '(':
            case ')':
            case '@':
            case ',':
            case '[':
            case ']':
            case '=':
               return false;
            default:
               break;
         }
      }
      return true;
   }

   public static MediaType parse(String type)
   {
      int typeIndex = type.indexOf('/');
      int paramIndex = type.indexOf(';');
      String major = null;
      String subtype = null;
      if (typeIndex < 0) // possible "*"
      {
         major = type;
         if (paramIndex > -1)
         {
            major = major.substring(0, paramIndex);
         }
         if (!MediaType.MEDIA_TYPE_WILDCARD.equals(major))
         {
            throw new IllegalArgumentException(Messages.MESSAGES.failureParsingMediaType(type));
         }
         subtype = MediaType.MEDIA_TYPE_WILDCARD;
      }
      else
      {
         major = type.substring(0, typeIndex);
         if (paramIndex > -1)
         {
            subtype = type.substring(typeIndex + 1, paramIndex);
         }
         else
         {
            subtype = type.substring(typeIndex + 1);
         }
      }
      if (major.length() < 1 || subtype.length() < 1)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.failureParsingMediaType(type));
      }
      if (!isValid(major) || !isValid(subtype))
      {
         throw new IllegalArgumentException(Messages.MESSAGES.failureParsingMediaType(type));
      }
      String params = null;
      if (paramIndex > -1) params = type.substring(paramIndex + 1);
      if (params != null && !params.equals(""))
      {
         HashMap<String, String> typeParams = new HashMap<String, String>();

         int start = 0;

         while (start < params.length())
         {
            start = HeaderParameterParser.setParam(typeParams, params, start);
         }
         return new MediaType(major, subtype, typeParams);
      }
      else
      {
         return new MediaType(major, subtype);
      }
   }

   private static final char[] quotedChars = "()<>@,;:\\\"/[]?= \t\r\n".toCharArray();

   public static boolean quoted(String str)
   {
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         for (char q : quotedChars) if (c == q) return true;
      }
      return false;
   }

   public String toString(Object o)
   {
      if (o == null) throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      MediaType type = (MediaType) o;
      StringBuilder buf = new StringBuilder();

      buf.append(type.getType().toLowerCase()).append("/").append(type.getSubtype().toLowerCase());
      if (type.getParameters() == null || type.getParameters().size() == 0) return buf.toString();
      for (String name : type.getParameters().keySet())
      {
         buf.append(';').append(name).append('=');
         String val = type.getParameters().get(name);
         if (quoted(val)) buf.append('"').append(val).append('"');
         else buf.append(val);
      }
      return buf.toString();
   }
}
