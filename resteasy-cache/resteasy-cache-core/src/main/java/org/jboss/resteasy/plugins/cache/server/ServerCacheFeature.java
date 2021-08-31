package org.jboss.resteasy.plugins.cache.server;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.MemoryConfiguration;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.cache.server.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyConfiguration;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerCacheFeature implements Feature
{
   protected ServerCache cache;

   public ServerCacheFeature()
   {
   }

   public ServerCacheFeature(final ServerCache cache)
   {
      this.cache = cache;
   }

   @Override
   public boolean configure(FeatureContext configurable)
   {
      ServerCache cache = getCache(configurable);
      if (cache == null) return false;
      configurable.register(new ServerCacheHitFilter(cache));
      configurable.register(new ServerCacheInterceptor(cache));
      return true;
   }

   protected ResteasyConfiguration getResteasyConfiguration()
   {
      return ResteasyContext.getContextData(ResteasyConfiguration.class);
   }

   protected String getConfigProperty(String name)
   {
      ResteasyConfiguration config = getResteasyConfiguration();
      if (config == null) return null;
      return config.getParameter(name);

   }

   protected ServerCache getCache(Configurable configurable)
   {
      if (this.cache != null) return this.cache;
      ServerCache c = (ServerCache)configurable.getConfiguration().getProperty(ServerCache.class.getName());
      if (c != null) return c;
      c = getXmlCache(configurable);
      if (c != null) return c;
      return getDefaultCache();
   }

   protected ServerCache getDefaultCache()
   {
      String RESTEASY_DEFAULT_CACHE = "resteasy-default-cache";
      ConfigurationBuilderHolder configBuilderHolder = new ConfigurationBuilderHolder();
      configBuilderHolder.getGlobalConfigurationBuilder()
              .defaultCacheName(RESTEASY_DEFAULT_CACHE)
              .jmx().enable()
              .build();
      configBuilderHolder.newConfigurationBuilder(RESTEASY_DEFAULT_CACHE)
              .memory()
              .maxCount(MemoryConfiguration.MAX_COUNT.getDefaultValue())
              .whenFull(EvictionStrategy.REMOVE)
              .maxCount(100)
              .build();
      EmbeddedCacheManager manager = new DefaultCacheManager(configBuilderHolder, true);
      Cache<Object, Object> c = manager.getCache(RESTEASY_DEFAULT_CACHE);
      return new InfinispanCache(c);
   }

   protected ServerCache getXmlCache(Configurable configurable)
   {
      String path = (String)configurable.getConfiguration().getProperty("server.request.cache.infinispan.config.file");
      if (path == null) path = getConfigProperty("server.request.cache.infinispan.config.file");
      if (path == null) return null;

      String name = (String)configurable.getConfiguration().getProperty("server.request.cache.infinispan.cache.name");
      if (name == null) name = getConfigProperty("server.request.cache.infinispan.cache.name");
      if (name == null) throw new RuntimeException(Messages.MESSAGES.needToSpecifyCacheName());

      try
      {
         Cache<Object, Object> c = new DefaultCacheManager(path).getCache(name);
         return new InfinispanCache(c);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
