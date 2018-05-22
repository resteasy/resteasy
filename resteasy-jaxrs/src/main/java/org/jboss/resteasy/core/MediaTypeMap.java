package org.jboss.resteasy.core;

import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Efficient MediaType index of T.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeMap<T>
{
   public static interface Typed
   {
      Class<?> getType();
   }

   private static class TypedEntryComparator implements Comparator<Entry<?>>, Serializable
   {
      private static final long serialVersionUID = -8815419198743440920L;
      private Class<?> type;

      public TypedEntryComparator(Class<?> type)
      {
         this.type = type;
      }

      private boolean isAssignableFrom(Typed typed)
      {
         if (typed.getType() == null) return false;
         return typed.getType().isAssignableFrom(type);
      }

      private int compareTypes(Entry<?> entry, Entry<?> entry1)
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

      public int compare(Entry<?> entry, Entry<?> entry1)
      {
         int val = compareTypes(entry, entry1);
         if (val == 0) val = entry.compareTo(entry1);
         return val;
      }
   }

   private static class Entry<T> implements Comparable<Entry<?>>
   {
      public MediaType mediaType;
      public T object;

      private Entry(MediaType mediaType, T object)
      {
         this.mediaType = mediaType;
         this.object = object;
      }

      @SuppressWarnings({"rawtypes", "unchecked"})
      public int compareTo(Entry<?> entry)
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
   public static Pattern COMPOSITE_SUBTYPE_WILDCARD_PATTERN = Pattern.compile("\\*\\+(.+)");


   // This composite is subtype+*  i.e. atom+* rss+*
   public static Pattern WILD_SUBTYPE_COMPOSITE_PATTERN = Pattern.compile("([^\\+]+)\\+\\*");

   private static class SubtypeMap<T>
   {
      private Map<String, List<Entry<T>>> index = new ConcurrentHashMap<String, java.util.List<Entry<T>>>();
      private Map<String, List<Entry<T>>> compositeIndex = new ConcurrentHashMap<String, List<Entry<T>>>();
      private Map<String, List<Entry<T>>> wildCompositeIndex = new ConcurrentHashMap<String, List<Entry<T>>>();
      private List<Entry<T>> wildcards = new CopyOnWriteArrayList<Entry<T>>();
      private List<Entry<T>> all = new CopyOnWriteArrayList<Entry<T>>();

      public SubtypeMap<T> clone()
      {
         SubtypeMap<T> clone = new SubtypeMap<T>();
         for (Map.Entry<String, List<Entry<T>>> entry : index.entrySet())
         {
            List<Entry<T>> newList = new CopyOnWriteArrayList<Entry<T>>();
            newList.addAll(entry.getValue());
            clone.index.put(entry.getKey(), newList);
         }
         for (Map.Entry<String, List<Entry<T>>> entry : compositeIndex.entrySet())
         {
            List<Entry<T>> newList = new CopyOnWriteArrayList<Entry<T>>();
            newList.addAll(entry.getValue());
            clone.compositeIndex.put(entry.getKey(), newList);
         }
         for (Map.Entry<String, List<Entry<T>>> entry : wildCompositeIndex.entrySet())
         {
            List<Entry<T>> newList = new CopyOnWriteArrayList<Entry<T>>();
            newList.addAll(entry.getValue());
            clone.wildCompositeIndex.put(entry.getKey(), newList);
         }
         clone.wildcards.addAll(wildcards);
         clone.all.addAll(all);
         return clone;
      }


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
               list = new CopyOnWriteArrayList<Entry<T>>();
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
               list = new CopyOnWriteArrayList<Entry<T>>();
               wildCompositeIndex.put(main, list);
            }
            list.add(entry);
         }
         else
         {
            List<Entry<T>> list = index.get(type.getSubtype());
            if (list == null)
            {
               list = new CopyOnWriteArrayList<Entry<T>>();
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

   private Map<String, SubtypeMap<T>> index = new ConcurrentHashMap<String, MediaTypeMap.SubtypeMap<T>>();
   private volatile List<Entry<T>> wildcards = new ArrayList<Entry<T>>();
   private volatile List<Entry<T>> all = new ArrayList<Entry<T>>();
   private volatile List<T> everything = new ArrayList<T>();
   private Map<CachedMediaTypeAndClass, List<T>> classCache = new ConcurrentHashMap<CachedMediaTypeAndClass, List<T>>();

   public MediaTypeMap<T> clone()
   {
      MediaTypeMap<T> clone = new MediaTypeMap<T>();
      for (Map.Entry<String, SubtypeMap<T>> entry : index.entrySet())
      {
         clone.index.put(entry.getKey(), entry.getValue().clone());
      }
      clone.wildcards.addAll(wildcards);
      clone.all.addAll(all);
      clone.everything.addAll(everything);
      // don't clone class cache
      return clone;
   }

   public Map<CachedMediaTypeAndClass, List<T>> getClassCache()
   {
      return classCache;
   }



   public static class CachedMediaTypeAndClass
   {
      // we need a weak reference because of possible hot deployment
      // Although, these reference should get cleared up with any add() invocation
      private WeakReference<Class<?>> clazz;
      private MediaType mediaType;
      private final int hash;

      @SuppressWarnings({"rawtypes", "unchecked"})
      private CachedMediaTypeAndClass(Class clazz, MediaType mediaType)
      {
         this.clazz = new WeakReference(clazz);
         this.mediaType = mediaType;
         int result = clazz.hashCode();
         result = 31 * result + (mediaType.getType() != null ? mediaType.getType().hashCode() : 0) +  (mediaType.getSubtype() != null ? mediaType.getSubtype().hashCode() : 0);
         hash = result;
      }

      private Class<?> getClazz()
      {
         return clazz.get();
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         CachedMediaTypeAndClass that = (CachedMediaTypeAndClass) o;

         // WeakReference may have GC'd
         Class<?> clazz = getClazz();
         if (clazz == null || that.getClazz() == null) return false;

         if (!clazz.equals(that.getClazz())) return false;

         if (mediaType.getType() != null)
         {
            if (!mediaType.getType().equals(that.mediaType.getType())) return false;
         }
         else if ((mediaType.getType() != that.mediaType.getType())) return false;

         if (mediaType.getSubtype() != null)
         {
            if (!mediaType.getSubtype().equals(that.mediaType.getSubtype())) return false;
         }
         else if ((mediaType.getSubtype() != that.mediaType.getSubtype())) return false;

         return true;
      }

      @Override
      public int hashCode()
      {
         return hash;
      }
   }

   /**
    * Add an object to the media type map.  This is synchronized to serialize adds.
    *
    * @param type media type
    * @param obj object
    */
   public synchronized void add(MediaType type, T obj)
   {
      classCache.clear();
      type = new MediaType(type.getType().toLowerCase(), type.getSubtype().toLowerCase(), type.getParameters());
      Entry<T> entry = new Entry<T>(type, obj);
      List<Entry<T>> newall = new ArrayList<Entry<T>>(all.size() + 1);
      newall.addAll(all);
      newall.add(entry);
      Collections.sort(newall);
      all = newall;
      everything = convert(newall);

      if (type.isWildcardType())
      {
         List<Entry<T>> newwildcards = new ArrayList<Entry<T>>(wildcards.size() + 1);
         newwildcards.addAll(wildcards);
         newwildcards.add(entry);
         wildcards = newwildcards;
      }
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
      List<T> newList = new ArrayList<T>(list.size());
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
    * @return list of objects
    */
   public List<T> getPossible(MediaType accept)
   {
      accept = new MediaType(accept.getType().toLowerCase(), accept.getSubtype().toLowerCase(), accept.getParameters());
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

   /**
    * By default, MediaTypeMap will cache possible MediaType/Class matches.  Set this to false to turn off
    * caching
    *
    */
   public static boolean useCache = true;

   public List<T> getPossible(MediaType accept, Class<?> type)
   {
      List<T> cached = null;
      CachedMediaTypeAndClass cacheEntry = null;
      if (useCache)
      {
         cacheEntry = new CachedMediaTypeAndClass(type, accept);
         cached = classCache.get(cacheEntry);
         if (cached != null) return cached;
      }

      accept = new MediaType(accept.getType().toLowerCase(), accept.getSubtype().toLowerCase(), accept.getParameters());
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
      cached = convert(matches);
      if (useCache) classCache.put(cacheEntry, cached);
      return cached;

   }

}
