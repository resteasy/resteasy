package org.jboss.resteasy.core;

import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * efficient MediaType index of T
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeMap<T>
{
   private static class Entry<T> implements Comparable<Entry>
   {
      public MediaType mediaType;
      public T object;

      private Entry(MediaType mediaType, T object)
      {
         this.mediaType = mediaType;
         this.object = object;
      }

      public int compareTo(Entry entry)
      {
         return MediaTypeHelper.compareWeight(mediaType, entry.mediaType);
      }
   }

   private static class SubtypeMap<T>
   {
      private Map<String, List<Entry<T>>> index = new HashMap<String, List<Entry<T>>>();
      private List<Entry<T>> wildcards = new ArrayList<Entry<T>>();
      private List<Entry<T>> all = new ArrayList<Entry<T>>();


      public void add(MediaType type, T obj)
      {
         Entry<T> entry = new Entry<T>(type, obj);
         all.add(entry);

         if (type.isWildcardSubtype()) wildcards.add(entry);
         else
         {
            List<Entry<T>> list = index.get(type.getSubtype());
            if (list == null)
            {
               list = new ArrayList<Entry<T>>();
               index.put(type.getSubtype(), list);
            }
            list.add(entry);
         }
      }

      public List<Entry<T>> getPossible(MediaType accept)
      {
         if (accept.isWildcardSubtype())
         {
            return all;
         }
         else
         {
            List<Entry<T>> matches = new ArrayList<Entry<T>>();
            matches.addAll(wildcards);
            List<Entry<T>> indexed = index.get(accept.getSubtype());
            if (indexed != null) matches.addAll(indexed);
            return matches;
         }
      }
   }

   private Map<String, SubtypeMap<T>> index = new HashMap<String, SubtypeMap<T>>();
   private List<Entry<T>> wildcards = new ArrayList<Entry<T>>();
   private List<Entry<T>> all = new ArrayList<Entry<T>>();
   private List<T> everything = new ArrayList<T>();

   public void add(MediaType type, T obj)
   {
      Entry<T> entry = new Entry<T>(type, obj);
      all.add(entry);
      Collections.sort(all);
      everything = convert(all);

      if (type.isWildcardType()) wildcards.add(entry);
      else
      {
         SubtypeMap<T> subtype = index.get(type.getType());
         if (subtype == null)
         {
            subtype = new SubtypeMap<T>();
            index.put(type.getType(), subtype);
         }
         subtype.add(type, obj);
      }
   }


   private static <T> List<T> convert(List<Entry<T>> list)
   {
      ArrayList<T> newList = new ArrayList<T>(list.size());
      for (Entry<T> entry : list)
      {
         newList.add(entry.object);
      }
      return newList;

   }

   /**
    * Returns a list of objects sorted based on their media type where the first in the list
    * is the best match
    *
    * @param accept mime to match
    * @return
    */
   public List<T> getPossible(MediaType accept)
   {
      List<Entry<T>> matches = new ArrayList<Entry<T>>();
      if (accept.isWildcardType())
      {
         ArrayList<T> copy = new ArrayList<T>();
         copy.addAll(everything);
         return copy;
      }
      else
      {
         matches.addAll(wildcards);
         SubtypeMap<T> indexed = index.get(accept.getType());
         if (indexed != null)
         {
            matches.addAll(indexed.getPossible(accept));
         }
      }
      Collections.sort(matches);
      return convert(matches);
   }

}
