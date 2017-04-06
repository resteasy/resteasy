package org.jboss.resteasy.skeleton.key.idm.service;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.resteasy.skeleton.key.SkeletonKeyContextResolver;
import org.jboss.resteasy.skeleton.key.idm.adapters.infinispan.InfinispanIDM;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ApplicationPath("/")
public class SkeletonKeyApplication extends Application
{
   protected Set<Object> singletons = new HashSet<Object>();
   protected Set<Class<?>> classes = new HashSet<Class<?>>();

   public SkeletonKeyApplication()
   {
      Cache cache = getCache();
      InfinispanIDM idm = new InfinispanIDM(cache);
      singletons.add(new TokenManagement(idm));
      singletons.add(new RealmFactory(idm));
      singletons.add(new RealmResource(idm));
      classes.add(SkeletonKeyContextResolver.class);
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

   protected Cache getCache()
   {
      try
      {
         InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("skeleton-key.xml");
         return new DefaultCacheManager(is).getCache("skeleton-key");
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
