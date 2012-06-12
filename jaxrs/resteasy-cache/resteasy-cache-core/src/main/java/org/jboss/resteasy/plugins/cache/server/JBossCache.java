package org.jboss.resteasy.plugins.cache.server;


import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.RegionManagerImpl;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.EvictionConfig;
import org.jboss.cache.config.EvictionRegionConfig;
import org.jboss.cache.eviction.ExpirationAlgorithmConfig;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * An HTTP cache that behaves somewhat the same way as a proxy (like Squid)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JBossCache implements ServerCache
{

   public static class CacheEntry implements Entry
   {
      private final byte[] cached;
      private final int expires;
      private final long timestamp = System.currentTimeMillis();
      private final MultivaluedMap<String, Object> headers;
      private final String etag;
      private final MediaType mediaType;

      private CacheEntry(MultivaluedMap<String, Object> headers, byte[] cached, int expires, String etag, MediaType mediaType)
      {
         this.cached = cached;
         this.expires = expires;
         this.headers = headers;
         this.etag = etag;
         this.mediaType = mediaType;
      }

      public int getExpirationInSeconds()
      {
         return expires - (int) ((System.currentTimeMillis() - timestamp) / 1000);
      }

      public boolean isExpired()
      {
         return System.currentTimeMillis() - timestamp >= expires * 1000;
      }

      public String getEtag()
      {
         return etag;
      }

      public MultivaluedMap<String, Object> getHeaders()
      {
         return headers;
      }

      public byte[] getCached()
      {
         return cached;
      }

      public MediaType getMediaType()
      {
         return mediaType;
      }
   }

   protected int maxSize = 100;
   protected long wakeupInterval = 5000;
   protected Cache cache;
   protected ResteasyProviderFactory providerFactory;

   public int getMaxSize()
   {
      return maxSize;
   }

   public void setMaxSize(int maxSize)
   {
      this.maxSize = maxSize;
   }

   public long getWakeupInterval()
   {
      return wakeupInterval;
   }

   public void setWakeupInterval(long wakeupInterval)
   {
      this.wakeupInterval = wakeupInterval;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void start()
   {
      CacheFactory factory = new DefaultCacheFactory();
      ExpirationAlgorithmConfig exp = new ExpirationAlgorithmConfig();
      exp.setMaxNodes(maxSize);

      EvictionRegionConfig evictionRegionConfig = new EvictionRegionConfig(RegionManagerImpl.DEFAULT_REGION);
      evictionRegionConfig.setEvictionAlgorithmConfig(exp);

      EvictionConfig evictConfig = new EvictionConfig();
      evictConfig.setDefaultEvictionRegionConfig(evictionRegionConfig);
      evictConfig.setWakeupInterval(wakeupInterval);


      Configuration config = new Configuration();
      config.setCacheMode(Configuration.CacheMode.LOCAL);
      config.setEvictionConfig(evictConfig);

      cache = factory.createCache(config, true);

      ServerCacheHitInterceptor hit = new ServerCacheHitInterceptor(this);
      ServerCacheInterceptor interceptor = new ServerCacheInterceptor(this);

      getProviderFactory().getContainerRequestFilterRegistry().registerLegacy(hit);
      getProviderFactory().getServerWriterInterceptorRegistry().registerLegacy(interceptor);
   }

   public void stop()
   {
      cache.stop();
   }


   public Entry get(String uri, MediaType accept)
   {
      Node parent = cache.getRoot().getChild(Fqn.fromElements(uri));
      if (parent == null) return null;

      for (Object obj : parent.getChildren())
      {
         Node leaf = (Node) obj;
         CacheEntry entry = (CacheEntry) leaf.get("entry");
         if (entry == null) continue;
         if (accept.isCompatible(entry.getMediaType()))
         {
            return entry;
         }
      }
      return null;
   }

   public Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag)
   {
      // there's a race condition here with a concurrent get() method above.  Too bad JBoss Cache doesn't have a way to create
      // a node before hand then insert it
      CacheEntry cacheEntry = new CacheEntry(headers, entity, cc.getMaxAge(), etag, mediaType);
      Node parent = cache.getRoot().addChild(Fqn.fromElements(uri));
      Node leaf = parent.addChild(Fqn.fromElements(mediaType.toString()));
      leaf.put("entry", cacheEntry);
      leaf.put(ExpirationAlgorithmConfig.EXPIRATION_KEY, (cc.getMaxAge() * 1000) + System.currentTimeMillis());
      return cacheEntry;
   }

   public void remove(String uri)
   {
      Node parent = cache.getRoot().getChild(Fqn.fromElements(uri));
      if (parent == null) return;
      cache.getRoot().removeChild(Fqn.fromElements(uri));

   }

   public void clear()
   {
      cache.clearData(Fqn.ROOT);
   }

}