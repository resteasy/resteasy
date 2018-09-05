package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class IgnoredMediaTypes
{
   private static String getVendorString(String subtype)
   {
      int idx = subtype.indexOf('+');
      if (idx < 0) return subtype;
      return subtype.substring(0, idx);
   }

   private static String getSubtype(String subtype)
   {
      int idx = subtype.indexOf('+');
      if (idx < 0) return subtype;
      return subtype.substring(idx + 1);
   }

   public static boolean ignored(Class<?> type, Annotation[] annotations, MediaType mediaType)
   {
      IgnoreMediaTypes ignore = FindAnnotation.findAnnotation(type, annotations, IgnoreMediaTypes.class);
      if (ignore == null) return false;
      for (String mime : ignore.value())
      {
         if (compare(mediaType, mime)) return true;
      }
      return false;
   }

   private static boolean compare(MediaType mediaType, String mime)
   {
      MediaType mt = MediaType.valueOf(mime);
      if (mt.isWildcardType() || mediaType.isWildcardType()) return true;
      if (!mediaType.getType().equals(mt.getType())) return false;
      if (mt.isWildcardSubtype() || mt.isWildcardSubtype()) return true;
      if (mt.getSubtype().startsWith("*+"))
      {
         String compare = getSubtype(mt.getSubtype());
         String compare2 = getSubtype(mediaType.getSubtype());
         return compare.equals(compare2);
      }
      else if (mt.getSubtype().endsWith("+*"))
      {
         String compare = getVendorString(mt.getSubtype());
         String compare2 = getVendorString(mediaType.getSubtype());
         return compare.equals(compare2);
      }
      else
      {
         return mt.getSubtype().equals(mediaType.getSubtype());
      }
   }

   public static void main(String[] args)
   {
      Logger LOG = Logger.getLogger(IgnoredMediaTypes.class);
      LOG.info(getVendorString("foo+json"));
      LOG.info(getSubtype("foo+json"));

   }

}
