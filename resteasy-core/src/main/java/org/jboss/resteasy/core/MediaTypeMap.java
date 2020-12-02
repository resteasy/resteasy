package org.jboss.resteasy.core;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.util.MediaTypeHelper;

import jakarta.ws.rs.core.MediaType;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A copy-on-write MediaType index of T.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeMap<T>
{
   public interface Typed
   {
      Class<?> getType();
   }

   private static class TypedEntryComparator implements Comparator<Entry<?>>, Serializable
   {
      private static final long serialVersionUID = -8815419198743440920L;
      private Class<?> type;

      TypedEntryComparator(final Class<?> type)
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

      private Entry(final MediaType mediaType, final T object)
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

   private static final Pattern COMPOSITE_PATTERN = Pattern.compile("([^\\+]+)\\+(.+)");

   // Composite subtypes are of the pattern *+subtype i.e. *+xml, *+json
   public static final Pattern COMPOSITE_SUBTYPE_WILDCARD_PATTERN = Pattern.compile("\\*\\+(.+)");


   // This composite is subtype+*  i.e. atom+* rss+*
   public static final Pattern WILD_SUBTYPE_COMPOSITE_PATTERN = Pattern.compile("([^\\+]+)\\+\\*");

   public static String compositeWildSubtype(String subtype) {
      final Matcher matcher = COMPOSITE_SUBTYPE_WILDCARD_PATTERN.matcher(subtype);
      if (matcher.matches()) {
         return matcher.group(1);
      }
      return null;
   }

   public static String wildCompositeSubtype(String subtype) {
      final Matcher matcher = WILD_SUBTYPE_COMPOSITE_PATTERN.matcher(subtype);
      if (matcher.matches()) {
         return matcher.group(1);
      }
      return null;
   }

   private class SubtypeMap<T>
   {
      private volatile Map<String, List<Entry<T>>> index;
      private volatile Map<String, List<Entry<T>>> compositeIndex;
      private volatile Map<String, List<Entry<T>>> wildCompositeIndex;
      private volatile List<Entry<T>> wildcards;
      private volatile List<Entry<T>> all;

      private SubtypeMap() {
         index = new HashMap<>();
         compositeIndex = new HashMap<>();
         wildCompositeIndex = new HashMap<>();
         wildcards = new ArrayList<>();
         all = new ArrayList<>();
      }

      private SubtypeMap(final SubtypeMap<T> subtypeMap) {
         index = subtypeMap.index;
         compositeIndex = subtypeMap.compositeIndex;
         wildCompositeIndex = subtypeMap.wildCompositeIndex;
         wildcards = subtypeMap.wildcards;
         all = subtypeMap.all;
      }


      private void add(Entry<T> entry)
      {
         final Matcher matcher = COMPOSITE_SUBTYPE_WILDCARD_PATTERN.matcher(entry.mediaType.getSubtype());
         final Matcher wildCompositeMatcher = WILD_SUBTYPE_COMPOSITE_PATTERN.matcher(entry.mediaType.getSubtype());


         if (entry.mediaType.isWildcardSubtype()) {
            addWildcard(entry);
         }
         else if (matcher.matches())
         {
            String baseSubType = matcher.group(1);
            addCompositeWild(entry, baseSubType);
         }
         else if (wildCompositeMatcher.matches())
         {
            String base = wildCompositeMatcher.group(1);
            addWildComposite(entry, base);
         }
         else
         {
            addRegular(entry);
         }
      }

      private void addRegular(Entry<T> entry) {
         Map<String, List<Entry<T>>> newIndex = index;
         if (lockSnapshots) newIndex = copy(index);
         add(newIndex, entry.mediaType.getSubtype(), entry);
         index = newIndex;
         merge(entry);
      }

      private void addWildComposite(Entry<T> entry, String base) {
         Map<String, List<Entry<T>>> newWildCompositeIndex = wildCompositeIndex;
         if (lockSnapshots) newWildCompositeIndex = (wildCompositeIndex);
         add(newWildCompositeIndex, base, entry);
         wildCompositeIndex = newWildCompositeIndex;
         merge(entry);
      }

      private void addCompositeWild(Entry<T> entry, String baseSubType) {
         Map<String, List<Entry<T>>> newCompositeIndex = compositeIndex;
         if (lockSnapshots) newCompositeIndex = copy(compositeIndex);
         add(newCompositeIndex, baseSubType, entry);
         compositeIndex = newCompositeIndex;
         merge(entry);
      }

      private void addWildcard(Entry<T> entry) {
         if (lockSnapshots) wildcards = copyAndAdd(wildcards, entry);
         else wildcards.add(entry);
         merge(entry);
      }

      private void merge(Entry<T> entry) {
         if (lockSnapshots) {
            all = copyAndAdd(all, entry);
         } else {
            all.add(entry);
         }
      }

      private Map<String, List<Entry<T>>> copy(final Map<String, List<Entry<T>>> original) {
         final Map<String, List<Entry<T>>> copy = new HashMap<>(original.size());
         original.forEach((key, value) -> copy.put(key, new ArrayList<>(value)));
         return copy;
      }

      private void add(final Map<String, List<Entry<T>>> map,
                       final String key,
                       final Entry<T> entry) {
         map.putIfAbsent(key, new CopyOnWriteArrayList<>());
         map.get(key).add(entry);
      }

      private List<Entry<T>> getPossible(MediaType accept)
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

   static <A> List<A> copyAndAdd(List<A> a, A entry) {
      // reduce internal array copying
      ArrayList<A> newList = new ArrayList<A>(a.size() + 1);
      newList.add(entry);
      newList.addAll(0, a);
      return newList;
   }

   private static class CachedMediaTypeAndClass
   {
      // we need a weak reference because of possible hot deployment
      // Although, these reference should get cleared up with any add() invocation
      private WeakReference<Class<?>> clazz;
      private MediaType mediaType;
      private final int hash;

      @SuppressWarnings({"rawtypes", "unchecked"})
      private CachedMediaTypeAndClass(final Class clazz, final MediaType mediaType)
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

   private volatile Map<String, SubtypeMap<T>> index;
   private volatile Map<CachedMediaTypeAndClass, List<T>> classCache;
   private volatile List<Entry<T>> wildcards;
   private volatile List<Entry<T>> everything;
   private boolean lockSnapshots;

   public MediaTypeMap() {
      index = new HashMap<>();
      wildcards = new ArrayList<>();
      everything = new ArrayList<>();
   }

   /**
    * Shallow copy, any additional adds will deep copy
    *
    * @param mediaTypeMap
    */
   public MediaTypeMap(final MediaTypeMap<T> mediaTypeMap) {
      lockSnapshots = true;
      index      = mediaTypeMap.index;
      wildcards  = mediaTypeMap.wildcards;
      everything        = mediaTypeMap.everything;
      classCache = mediaTypeMap.classCache;
   }

   /**
    * After this is called, all new adds will deep copy itself.
    *
    */
   public void lockSnapshots() {
      //if (!lockSnapshots) Collections.sort(everything);
      lockSnapshots = true;
   }

   /**
    * Add an object to the media type map.  This is synchronized to serialize adds.
    *
    * @param type media type
    * @param obj object
    */
   public synchronized void add(final MediaType type, final T obj)
   {
      final MediaType newType =
              new MediaType(type.getType().toLowerCase(), type.getSubtype().toLowerCase(), type.getParameters());
      final Entry<T> entry = new Entry<>(newType, obj);

      add(entry);
   }

   /**
    * Add an object to the media type map.  This is synchronized to serialize adds.
    *
    * @param mediaType media type
    * @param obj object
    */
   public synchronized void add(String mediaType, final T obj)
   {
      final MediaType newType = MediaTypeHeaderDelegate.parse(mediaType.toLowerCase());
      final Entry<T> entry = new Entry<>(newType, obj);
      add(entry);
   }

   public synchronized void addWildcard(final T obj) {
      final Entry<T> entry = new Entry<>(MediaType.WILDCARD_TYPE, obj);
      addWildcard(entry);
   }


   protected void add(Entry<T> entry) {
      if (entry.mediaType.isWildcardType())
      {
         addWildcard(entry);
      }
      else
      {
         Map<String, SubtypeMap<T>> newIndex = copyIndex();
         newIndex.putIfAbsent(entry.mediaType.getType(), new SubtypeMap<>());
         newIndex.get(entry.mediaType.getType()).add(entry);
         index = newIndex;
         mergeEverything(entry);
      }
   }

   private Map<String, SubtypeMap<T>> copyIndex() {
      Map<String, SubtypeMap<T>> newIndex = index;
      if (lockSnapshots) {
         Map<String, SubtypeMap<T>> finalIndex = new HashMap<>();
         newIndex = finalIndex;
         index.forEach((key, value) -> finalIndex.put(key, new SubtypeMap<>(value)));
      }
      return newIndex;
   }

   private void addWildcard(Entry<T> entry) {
      if (lockSnapshots) wildcards = copyAndAdd(wildcards, entry);
      else wildcards.add(entry);
      mergeEverything(entry);
   }

   public synchronized void addRegular(MediaType mediaType, T obj) {
      final Entry<T> entry = new Entry<>(mediaType, obj);
      Map<String, SubtypeMap<T>> newIndex = copyIndex();
      newIndex.putIfAbsent(entry.mediaType.getType(), new SubtypeMap<>());
      SubtypeMap<T> subtypeMap = newIndex.get(entry.mediaType.getType());
      subtypeMap.addRegular(entry);
      index = newIndex;
      mergeEverything(entry);
   }

   public synchronized void addCompositeWild(MediaType mediaType, T obj, String baseSubtype) {
      final Entry<T> entry = new Entry<>(mediaType, obj);
      Map<String, SubtypeMap<T>> newIndex = copyIndex();
      newIndex.putIfAbsent(entry.mediaType.getType(), new SubtypeMap<>());
      SubtypeMap<T> subtypeMap = newIndex.get(entry.mediaType.getType());

      subtypeMap.addCompositeWild(entry, baseSubtype);

      index = newIndex;
      mergeEverything(entry);
   }

   public synchronized void addWildComposite(MediaType mediaType, T obj, String baseSubtype) {
      final Entry<T> entry = new Entry<>(mediaType, obj);
      Map<String, SubtypeMap<T>> newIndex = copyIndex();
      newIndex.putIfAbsent(entry.mediaType.getType(), new SubtypeMap<>());
      SubtypeMap<T> subtypeMap = newIndex.get(entry.mediaType.getType());

      subtypeMap.addWildComposite(entry, baseSubtype);

      index = newIndex;
      mergeEverything(entry);
   }

   public synchronized void addWildSubtype(MediaType mediaType, T obj) {
      final Entry<T> entry = new Entry<>(mediaType, obj);
      Map<String, SubtypeMap<T>> newIndex = copyIndex();
      newIndex.putIfAbsent(entry.mediaType.getType(), new SubtypeMap<>());
      SubtypeMap<T> subtypeMap = newIndex.get(entry.mediaType.getType());

      subtypeMap.addWildcard(entry);

      index = newIndex;
      mergeEverything(entry);
   }

   private void mergeEverything(Entry<T> entry) {
      List<Entry<T>> newAll = everything;
      if (lockSnapshots) {
         newAll = copyAndAdd(everything, entry);
         Collections.sort(newAll);
         everything = newAll;
      } else {
         everything.add(entry);
         Collections.sort(everything);
      }
      classCache = null;
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
         return convert(everything);
      }
      else
      {
         SubtypeMap<T> indexed = index.get(accept.getType());
         if (indexed != null)
         {
            matches.addAll(indexed.getPossible(accept));
         }
         matches.addAll(wildcards);
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
         if (classCache != null) {
            cached = classCache.get(cacheEntry);
            if (cached != null) return cached;
         }
      }

      accept = new MediaType(accept.getType().toLowerCase(), accept.getSubtype().toLowerCase(), accept.getParameters());
      List<Entry<T>> matches = new ArrayList<Entry<T>>();
      if (accept.isWildcardType())
      {
         matches.addAll(everything);
      }
      else
      {
         SubtypeMap<T> indexed = index.get(accept.getType());
         if (indexed != null)
         {
            matches.addAll(indexed.getPossible(accept));
         }
         matches.addAll(wildcards);
      }
      Collections.sort(matches, new TypedEntryComparator(type));
      cached = convert(matches);
      if (useCache) {
         Map<CachedMediaTypeAndClass, List<T>> cache = classCache;
         if (cache == null) {
            synchronized (this)
            {
               if (classCache == null)
               {
                  classCache = new HashMap<>();
               }
               cache = classCache;
            }
         }
         cache.put(cacheEntry, cached);
      }
      return cached;

   }

}
