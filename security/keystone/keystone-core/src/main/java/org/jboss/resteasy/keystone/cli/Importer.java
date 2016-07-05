package org.jboss.resteasy.keystone.cli;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.resteasy.keystone.server.Loader;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Importer
{
   public static void main(String[] args) throws Exception
   {
      if (args.length < 2)
      {
         System.err.println("java Importer xml cache-name");
         return;
      }
      String path = args[0];
      String name = args[1];
      Cache cache = new DefaultCacheManager(path).getCache(name);
      new Loader().importStore(System.in, cache);
      cache.stop();
   }
}
