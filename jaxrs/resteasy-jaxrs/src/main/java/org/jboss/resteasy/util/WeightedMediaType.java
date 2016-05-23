package org.jboss.resteasy.util;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.LoggableFailure;

import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WeightedMediaType extends MediaType implements Comparable<WeightedMediaType>
{
   private float weight = 1.0f;


   private WeightedMediaType(String type, String subtype, Map<String, String> parameters)
   {
      super(type, subtype, parameters);
   }

   public float getWeight()
   {
      return weight;
   }


   public int compareTo(WeightedMediaType o)
   {
      WeightedMediaType type2 = this;
      WeightedMediaType type1 = o;

      if (type1.weight < type2.weight) return -1;
      if (type1.weight > type2.weight) return 1;


      if (type1.isWildcardType() && !type2.isWildcardType()) return -1;
      if (!type1.isWildcardType() && type2.isWildcardType()) return 1;
      if (type1.isWildcardSubtype() && !type2.isWildcardSubtype()) return -1;
      if (!type1.isWildcardSubtype() && type2.isWildcardSubtype()) return 1;

      int numNonQ = 0;
      if (type1.getParameters() != null)
      {
         numNonQ = type1.getParameters().size();
      }

      int numNonQ2 = 0;
      if (type2.getParameters() != null)
      {
         numNonQ2 = type2.getParameters().size();
      }

      if (numNonQ < numNonQ2) return -1;
      if (numNonQ > numNonQ2) return 1;


      return 0;
   }

   /**
    * Non-equal properties should not be compatible
    */
   @Override
   public boolean isCompatible(MediaType other)
   {
      boolean result;
      if (other == null)
         result = false;
      if (getType().equals(MEDIA_TYPE_WILDCARD) || (other != null && other.getType().equals(MEDIA_TYPE_WILDCARD)))
         result = true;
      else if (other != null && getType().equalsIgnoreCase(other.getType()) && (getSubtype().equals(MEDIA_TYPE_WILDCARD) || (other != null && other.getSubtype().equals(MEDIA_TYPE_WILDCARD))))
         result = true;
      else
      {
         if (other!= null && getType().equalsIgnoreCase(other.getType())
                 && this.getSubtype().equalsIgnoreCase(other.getSubtype()))
         {
            if (getParameters() == null || getParameters().size() == 0)
            {
               result = true;
            }
            else
            {
               result = this.equals(other);
            }
         }
         else
         {
            result = false;
         }
      }
      return result;
   }

   public static WeightedMediaType valueOf(String type)
   {
      MediaType tmp = MediaTypeHeaderDelegate.parse(type);
      if (tmp.getParameters() == null || !tmp.getParameters().containsKey("q"))
      {
         return new WeightedMediaType(tmp.getType(), tmp.getSubtype(), tmp.getParameters());
      }
      HashMap<String, String> params = new HashMap<String, String>();
      params.putAll(tmp.getParameters());
      String q = params.remove("q");


      WeightedMediaType mediaType = new WeightedMediaType(tmp.getType(), tmp.getSubtype(), params);
      mediaType.weight = getQWithParamInfo(mediaType, q);

      return mediaType;

   }

   public static WeightedMediaType parse(MediaType tmp)
   {
      if (tmp.getParameters() == null || !tmp.getParameters().containsKey("q"))
      {
         return new WeightedMediaType(tmp.getType(), tmp.getSubtype(), tmp.getParameters());
      }
      HashMap<String, String> params = new HashMap<String, String>();
      params.putAll(tmp.getParameters());
      String q = params.remove("q");


      WeightedMediaType mediaType = new WeightedMediaType(tmp.getType(), tmp.getSubtype(), params);
      mediaType.weight = getQWithParamInfo(mediaType, q);

      return mediaType;

   }

   private static float getQWithParamInfo(MediaType type, String val)
   {
      try
      {
         if (val != null)
         {
            float rtn = Float.valueOf(val);
//            if (rtn > 1.0F)
//               throw new LoggableFailure("MediaType q value cannot be greater than 1.0: " + type.toString(), HttpResponseCodes.SC_BAD_REQUEST);
            return rtn;
         }
      }
      catch (NumberFormatException e)
      {
         throw new LoggableFailure(Messages.MESSAGES.mediaTypeQMustBeFloat(type), HttpResponseCodes.SC_BAD_REQUEST);
      }
      return 1.0f;
   }

   @Override
   public boolean equals(Object obj)
   {
      return super.equals(obj);
   }

}
