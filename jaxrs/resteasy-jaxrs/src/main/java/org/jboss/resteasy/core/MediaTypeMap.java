package org.jboss.resteasy.core;

import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
   public static interface Typed
   {
      Class getType();
   }

   public static class TypedEntryComparator implements Comparator<Entry>
   {
      private Class type;

      public TypedEntryComparator(Class type)
      {
         this.type = type;
      }

      private boolean isAssignableFrom(Typed typed)
      {
         if (typed.getType() == null) return false;
         return typed.getType().isAssignableFrom(type);
      }

      private int compareTypes(Entry entry, Entry entry1)
      {
         int val = 0;
         if (entry.object instanceof Typed && entry1.object instanceof Typed && type != null)
         {
            Typed one = (Typed) entry.object;
            Typed two = (Typed) entry1.object;


            boolean oneTyped = isAssignableFrom(one);
            boolean twoTyped = isAssignableFrom(two);
            if (oneTyped == twoTyped && (!oneTyped && !twoTyped))
            {
               // both are false
               val = 0;
            }
            else if (oneTyped == twoTyped)
            {
               // both are true.
               // test for better assignability
               if (one.getType().equals(two.getType()))
               {
                  val = 0;
               }
               else if (one.getType().isAssignableFrom(two.getType()))
               {
                  val = 1;
               }
               else
               {
                  val = -1;
               }
            }
            else if (oneTyped) val = -1;
            else val = 1;
         }
         return val;

      }

      public int compare(Entry entry, Entry entry1)
      {
         int val = compareTypes(entry, entry1);
         if (val == 0) val = entry.compareTo(entry1);
         return val;
      }
   }

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
         int val = MediaTypeHelper.compareWeight(mediaType, entry.mediaType);
         if (val == 0 && object instanceof Comparable && entry.object instanceof Comparable)
         {
            return ((Comparable) object).compareTo(entry.object);
         }
         return val;
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

   public List<T> getPossible(MediaType accept, Class type)
   {
      List<Entry<T>> matches = new ArrayList<Entry<T>>();
      if (accept.isWildcardType())
      {
         matches.addAll(all);
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
      Collections.sort(matches, new TypedEntryComparator(type));
      return convert(matches);

   }

}
