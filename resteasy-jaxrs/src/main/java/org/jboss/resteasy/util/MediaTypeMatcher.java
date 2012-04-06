package org.jboss.resteasy.util;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class that picks an object from a MediaType map from a list of sorted acceptable MediaTypes
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeMatcher<T>
{
   protected Map<MediaType, T> representations;

   public Map<MediaType, T> getRepresentations()
   {
      return representations;
   }

   public void setRepresentations(Map<MediaType, T> representations)
   {
      this.representations = representations;
   }

   public T match(List<MediaType> accepts)
   {
      List<WeightedMediaType> convertedAccepts = new ArrayList<WeightedMediaType>();
      for (MediaType accept : accepts) convertedAccepts.add(WeightedMediaType.parse(accept));

      IdentityHashMap<WeightedMediaType, T> consumesMap = new IdentityHashMap<WeightedMediaType, T>();

      for (Map.Entry<MediaType, T> entry : representations.entrySet())
      {
         for (WeightedMediaType accept : convertedAccepts)
         {
            if (entry.getKey().isCompatible(accept))
            {
               consumesMap.put(accept, entry.getValue());
               break;
            }
         }
      }
      if (consumesMap.size() == 0)
      {
         return null;
      }
      if (consumesMap.size() == 1) return consumesMap.values().iterator().next();

      ArrayList<WeightedMediaType> consumes = new ArrayList<WeightedMediaType>();
      consumes.addAll(consumesMap.keySet());
      Collections.sort(consumes);

      return consumesMap.get(consumes.get(0));
   }
}
