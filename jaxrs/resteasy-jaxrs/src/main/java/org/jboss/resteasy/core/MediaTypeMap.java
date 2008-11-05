package org.jboss.resteasy.core;

import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

   private static Pattern COMPOSITE_PATTERN = Pattern.compile("([^\\+]+)\\+(.+)");

   // Composite subtypes are of the pattern *+subtype i.e. *+xml, *+json
   private static Pattern COMPOSITE_SUBTYPE_WILDCARD_PATTERN = Pattern.compile("\\*\\+(.+)");


   // This composite is subtype+*  i.e. atom+* rss+*
   private static Pattern WILD_SUBTYPE_COMPOSITE_PATTERN = Pattern.compile("([^\\+]+)\\+\\*");

   private static class SubtypeMap<T>
   {
      private Map<String, List<Entry<T>>> index = new HashMap<String, List<Entry<T>>>();
      private Map<String, List<Entry<T>>> compositeIndex = new HashMap<String, List<Entry<T>>>();
      private Map<String, List<Entry<T>>> wildCompositeIndex = new HashMap<String, List<Entry<T>>>();
      private List<Entry<T>> wildcards = new ArrayList<Entry<T>>();
      private List<Entry<T>> all = new ArrayList<Entry<T>>();


      public void add(MediaType type, T obj)
      {
         Entry<T> entry = new Entry<T>(type, obj);
         all.add(entry);

         Matcher matcher = COMPOSITE_SUBTYPE_WILDCARD_PATTERN.matcher(type.getSubtype());
         Matcher wildCompositeMatcher = WILD_SUBTYPE_COMPOSITE_PATTERN.matcher(type.getSubtype());

         if (type.isWildcardSubtype()) wildcards.add(entry);
         else if (matcher.matches())
         {
            String main = matcher.group(1);
            List<Entry<T>> list = compositeIndex.get(main);
            if (list == null)
            {
               list = new ArrayList<Entry<T>>();
               compositeIndex.put(main, list);
            }
            list.add(entry);
         }
         else if (wildCompositeMatcher.matches())
         {
            String main = wildCompositeMatcher.group(1);
            List<Entry<T>> list = wildCompositeIndex.get(main);
            if (list == null)
            {
               list = new ArrayList<Entry<T>>();
               wildCompositeIndex.put(main, list);
            }
            list.add(entry);
         }
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

            List<Entry<T>> indexed = index.get(accept.getSubtype());
            if (indexed != null) matches.addAll(indexed);

            Matcher matcher = COMPOSITE_PATTERN.matcher(accept.getSubtype());
            String compositeKey = accept.getSubtype();
            if (matcher.matches())
            {
               String wildCompositeKey = matcher.group(1);
               List<Entry<T>> windex = wildCompositeIndex.get(wildCompositeKey);
               if (windex != null) matches.addAll(windex);
               compositeKey = matcher.group(2);
            }
            List<Entry<T>> indexed2 = compositeIndex.get(compositeKey);
            if (indexed2 != null) matches.addAll(indexed2);
            matches.addAll(wildcards);
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
