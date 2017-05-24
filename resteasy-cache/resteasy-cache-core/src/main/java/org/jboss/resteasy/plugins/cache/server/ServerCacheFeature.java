package org.jboss.resteasy.plugins.cache.server;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.plugins.cache.server.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

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

   public ServerCacheFeature(ServerCache cache)
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
      return ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
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
      GlobalConfiguration gconfig = new GlobalConfigurationBuilder()
         .globalJmxStatistics()
         .allowDuplicateDomains(true)
         .enable()
         .jmxDomain("custom-cache")
         .build();
      Configuration configuration = new ConfigurationBuilder()
         .eviction()
         .strategy(EvictionStrategy.LIRS)
         .maxEntries(100)
         .jmxStatistics().enable()
         .build();
      EmbeddedCacheManager manager = new DefaultCacheManager(gconfig, configuration);
      Cache<Object, Object> c = manager.getCache("custom-cache");
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
