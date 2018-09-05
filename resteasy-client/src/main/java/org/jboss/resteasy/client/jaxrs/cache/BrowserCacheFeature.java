package org.jboss.resteasy.client.jaxrs.cache;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BrowserCacheFeature implements Feature
{
   protected BrowserCache cache;

   public BrowserCache getCache()
   {
      return cache;
   }

   public void setCache(BrowserCache cache)
   {
      this.cache = cache;
   }

   @Override
   public boolean configure(FeatureContext configuration)
   {
      if (cache == null) cache = new LightweightBrowserCache();
      configuration.property(BrowserCache.class.getName(), cache);
      configuration.register(new CacheInterceptor(cache));
      return true;
   }
}
