package org.resteasy.util;

import org.resteasy.Failure;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHelper
{
   public static MediaType getConsumes(Class declaring, Method method)
   {
      ConsumeMime consume = method.getAnnotation(ConsumeMime.class);
      if (consume == null)
      {
         consume = (ConsumeMime) declaring.getAnnotation(ConsumeMime.class);
      }
      if (consume == null) return null;
      return MediaType.parse(consume.value()[0]);
   }

   public static MediaType getProduces(Class declaring, Method method)
   {
      ProduceMime consume = method.getAnnotation(ProduceMime.class);
      if (consume == null)
      {
         consume = (ProduceMime) declaring.getAnnotation(ProduceMime.class);
      }
      if (consume == null) return null;
      return MediaType.parse(consume.value()[0]);
   }

   public static float getQ(MediaType type)
   {
      float rtn = getQWithParamInfo(type);
      if (rtn == 2.0F) return 1.0F;
      return rtn;
   }

   public static float getQWithParamInfo(MediaType type)
   {
      if (type.getParameters() != null)
      {
         String val = type.getParameters().get("q");
         try
         {
            if (val != null)
            {
               float rtn = Float.valueOf(val);
               if (rtn > 1.0F)
                  throw new Failure("MediaType q value cannot be greater than 1.0: " + type.toString(), HttpResponseCodes.SC_BAD_REQUEST);
               return rtn;
            }
         }
         catch (NumberFormatException e)
         {
            throw new RuntimeException("MediaType q parameter must be a float: " + type, e);
         }
      }
      return 2.0f;
   }

   private static class MediaTypeComparator implements Comparator<MediaType>
   {

      public int compare(MediaType mediaType2, MediaType mediaType)
      {
         float q = getQWithParamInfo(mediaType);
         boolean wasQ = q != 2.0f;
         if (q == 2.0f) q = 1.0f;

         float q2 = getQWithParamInfo(mediaType2);
         boolean wasQ2 = q2 != 2.0f;
         if (q2 == 2.0f) q2 = 1.0f;


         if (q < q2) return -1;
         if (q > q2) return 1;

         if (mediaType.isWildcardType() && !mediaType2.isWildcardType()) return -1;
         if (!mediaType.isWildcardType() && mediaType2.isWildcardType()) return 1;
         if (mediaType.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) return -1;
         if (!mediaType.isWildcardSubtype() && mediaType2.isWildcardSubtype()) return 1;

         int numNonQ = 0;
         if (mediaType.getParameters() != null)
         {
            numNonQ = mediaType.getParameters().size();
            if (wasQ) numNonQ--;
         }

         int numNonQ2 = 0;
         if (mediaType2.getParameters() != null)
         {
            numNonQ2 = mediaType2.getParameters().size();
            if (wasQ2) numNonQ2--;
         }

         if (numNonQ < numNonQ2) return -1;
         if (numNonQ > numNonQ2) return 1;


         return 0;
      }
   }

   public static void sort(List<MediaType> types)
   {
      Collections.sort(types, new MediaTypeComparator());
   }

   public static MediaType getBestMatch(List<MediaType> desired, List<MediaType> provided)
   {
      for (MediaType desire : desired)
      {
         for (MediaType provide : provided)
         {
            if (provide.isCompatible(desire)) return provide;
         }
      }
      return null;
   }

   public static List<MediaType> parseHeader(String header)
   {
      ArrayList<MediaType> types = new ArrayList<MediaType>();
      String[] medias = header.split(",");
      for (int i = 0; i < medias.length; i++)
      {
         types.add(MediaType.parse(medias[i].trim()));
      }
      return types;
   }
}
