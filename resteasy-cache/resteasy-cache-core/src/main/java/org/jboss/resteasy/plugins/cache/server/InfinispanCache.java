package org.jboss.resteasy.plugins.cache.server;


import org.infinispan.Cache;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * An HTTP cache that behaves somewhat the same way as a proxy (like Squid)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InfinispanCache implements ServerCache
{

   public static class CacheEntry implements Entry, Serializable
   {
      private static final long serialVersionUID = 2848638331930090578L;
      
      private byte[] cached;
      private int expires;
      private long timestamp = System.currentTimeMillis();
      private String etag;
      private transient MultivaluedMap<String, Object> headers;
      private transient MediaType mediaType;
      private transient MultivaluedMap<String, String> varyHeaders;
      
      private CacheEntry(MultivaluedMap<String, Object> headers, byte[] cached, int expires, String etag, MediaType mediaType, MultivaluedMap<String, String> varyHeaders)
      {
         this.cached = cached;
         this.expires = expires;
         this.headers = headers;
         this.etag = etag;
         this.mediaType = mediaType;
         this.varyHeaders = varyHeaders;
      }

      public int getExpirationInSeconds()
      {
         return expires - (int) ((System.currentTimeMillis() - timestamp) / 1000);
      }

      public boolean isExpired()
      {
         return System.currentTimeMillis() - timestamp >= expires * 1000L;
      }

      public String getEtag()
      {
         return etag;
      }

      public MultivaluedMap<String, Object> getHeaders()
      {
         return headers;
      }

      public MultivaluedMap<String, String> getVaryHeaders()
      {
         return varyHeaders;
      }

      public byte[] getCached()
      {
         return cached;
      }

      public MediaType getMediaType()
      {
         return mediaType;
      }
      
      private void writeObject(ObjectOutputStream stream) throws IOException
      {
         stream.defaultWriteObject();
         stream.writeObject(stringifyHeaders(headers));
         stream.writeObject(stringifyHeaders(varyHeaders));
         stream.writeUTF(mediaType.toString());
      }

      @SuppressWarnings("unchecked")
      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
      {
         stream.defaultReadObject();
         headers = unstringifyHeaders(MultivaluedMap.class.cast(stream.readObject()));
         varyHeaders = unstringifyHeaders(MultivaluedMap.class.cast(stream.readObject()));
         mediaType = MediaType.valueOf(stream.readUTF());
      }
   }

   @SuppressWarnings("rawtypes")
   protected Cache cache;

   @SuppressWarnings("rawtypes")
   public InfinispanCache(Cache cache)
   {
      this.cache = cache;
   }

   public Entry get(String uri, MediaType accept, MultivaluedMap<String, String> headers)
   {
      @SuppressWarnings("unchecked")
      Set<String> entries = (Set<String>)cache.get(uri);
      if (entries == null) return null;

      for (String entry : entries)
      {
         CacheEntry cacheEntry = (CacheEntry)cache.get(entry);
         if (cacheEntry == null) continue;
         if (accept.isCompatible(cacheEntry.getMediaType()) && !ServerCache.mayVary(cacheEntry, headers))
         {
            return cacheEntry;
         }
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag, MultivaluedMap<String, String> varyHeaders)
   {
      // there's a race condition here with a concurrent get() method above.  Too bad JBoss Cache doesn't have a way to create
      // a node before hand then insert it
      CacheEntry cacheEntry = new CacheEntry(headers, entity, cc.getMaxAge(), etag, mediaType, varyHeaders);
      StringBuffer varyHeadersString = new StringBuffer();
      varyHeaders.forEach((name, values) -> values.forEach(value -> varyHeadersString.append(name).append(value)));
      String entryName = uri + "    " + mediaType.toString() + "    " + varyHeadersString.toString();
      Set<String> entries = (Set<String>)cache.get(uri);
      Set<String> newEntries = new HashSet<String>();
      newEntries.add(entryName);
      if (entries != null)
      {
         newEntries.addAll(entries);
      }
      cache.put(uri, newEntries);
      cache.put(entryName, cacheEntry, cc.getMaxAge(), TimeUnit.SECONDS);
      return cacheEntry;
   }

   public void remove(String uri)
   {
      @SuppressWarnings("unchecked")
      Set<String> entries = (Set<String>)cache.remove(uri);
      if (entries == null) return;
      for (String entry : entries)
      {
         cache.remove(entry);
      }
   }

   public void clear()
   {
      cache.clear();
   }

   protected static MultivaluedMap<String, ?> stringifyHeaders(MultivaluedMap<String, ?> headers)
   {
      MultivaluedMap<String, Object> holders = new MultivaluedTreeMap<String, Object>();
      for (Iterator<String> it1 = headers.keySet().iterator(); it1.hasNext(); )
      {
         String key = it1.next();
         List<Object> outList = new ArrayList<Object>();
         holders.put(key, outList);
         List<?> list = headers.get(key);
         for (Iterator<?> it2 = list.iterator(); it2.hasNext(); )
         {
            Object o = it2.next();
            if (o instanceof CacheControl)
            {
               outList.add(new HeaderHolder(HeaderHolder.Type.CACHE_CONTROL, CacheControl.class.cast(o).toString()));
            }
            else if (o instanceof NewCookie)
            {
               outList.add(new HeaderHolder(HeaderHolder.Type.NEW_COOKIE, NewCookie.class.cast(o).toString()));
            }
            else if (o instanceof Cookie)
            {
               outList.add(new HeaderHolder(HeaderHolder.Type.COOKIE, Cookie.class.cast(o).toString()));     
            }
            else if (o instanceof EntityTag)
            {
               outList.add(new HeaderHolder(HeaderHolder.Type.ENTITY_TAG, EntityTag.class.cast(o).toString()));
            }
            else
            {
               outList.add(new HeaderHolder(HeaderHolder.Type.OTHER, o.toString()));
            }
         }
      }
      return holders;
   }
   

   protected static MultivaluedMap<String, Object> unstringifyHeaders(MultivaluedMap<String, Object> headers)
   {
      MultivaluedMap<String, Object> holders = new MultivaluedTreeMap<String, Object>();
      for (Iterator<String> it1 = headers.keySet().iterator(); it1.hasNext(); )
      {
         String key = it1.next();
         List<Object> outList = new ArrayList<Object>();
         holders.put(key, outList);
         List<Object> list = headers.get(key);
         for (Iterator<Object> it2 = list.iterator(); it2.hasNext(); )
         {  
            HeaderHolder holder = HeaderHolder.class.cast(it2.next());
            if (HeaderHolder.Type.CACHE_CONTROL.equals(holder.getType()))
            {
               outList.add(CacheControl.valueOf(holder.getValue()));
            }
            else if (HeaderHolder.Type.COOKIE.equals(holder.getType()))
            {
               outList.add(Cookie.valueOf(holder.getValue()));
            }
            else if (HeaderHolder.Type.ENTITY_TAG.equals(holder.getType()))
            {
               outList.add(EntityTag.valueOf(holder.getValue()));
            }
            else if (HeaderHolder.Type.NEW_COOKIE.equals(holder.getType()))
            {
               outList.add(NewCookie.valueOf(holder.getValue()));
            }
            else 
            {
               outList.add(holder.getValue());
            }
         }
      }
      return holders;
   }
}
