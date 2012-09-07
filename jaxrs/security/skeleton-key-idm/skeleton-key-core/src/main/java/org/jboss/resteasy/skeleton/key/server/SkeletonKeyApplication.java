package org.jboss.resteasy.skeleton.key.server;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyApplication extends Application
{
   public static final String SKELETON_KEY_INFINISPAN_CONFIG_FILE = "skeleton.key.infinispan.config.file";
   public static final String SKELETON_KEY_INFINISPAN_CACHE_NAME = "skeleton.key.infinispan.cache.name";
   Configurable configurable;
   protected Set<Object> singletons = new HashSet<Object>();

   protected RolesService roles;
   protected ProjectsService projects;
   protected UsersService users;
   protected TokenService tokenService;
   protected Cache cache;

   public SkeletonKeyApplication(@Context Configurable confgurable)
   {
      this.configurable = confgurable;
      String exp = getConfigProperty("skeleton.key.token.expiration");
      String unit = getConfigProperty("skeleton.key.token.expiration.unit");
      long expiration = (exp == null) ? 30 : Long.parseLong(exp);

      final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

      DEFAULT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

      final ObjectMapper WRAPPED_MAPPER = new ObjectMapper();

      WRAPPED_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      WRAPPED_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      WRAPPED_MAPPER.enable(SerializationConfig.Feature.WRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


      configurable.register(new ContextResolver<ObjectMapper>()
      {

         public ObjectMapper getContext(Class<?> type)
         {
            return type.getAnnotation(JsonRootName.class) == null ? DEFAULT_MAPPER : WRAPPED_MAPPER;
         }

      });

      cache = findCache();
      users = new UsersService(cache);
      singletons.add(users);
      roles = new RolesService(cache);
      singletons.add(roles);
      projects = new ProjectsService(cache, users, roles);
      singletons.add(projects);
      TimeUnit timeUnit = (unit == null) ? TimeUnit.MINUTES : TimeUnit.valueOf(unit);
      tokenService = new TokenService(cache, expiration, timeUnit, projects, users);
      singletons.add(tokenService);

      singletons.add(new TokenAuthFilter(tokenService));

   }

   public RolesService getRoles()
   {
      return roles;
   }

   public ProjectsService getProjects()
   {
      return projects;
   }

   public UsersService getUsers()
   {
      return users;
   }

   public TokenService getTokenService()
   {
      return tokenService;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

   protected ResteasyConfiguration getResteasyConfiguration()
   {
      return ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
   }

   protected String getConfigProperty(String name)
   {
      String val = (String)configurable.getProperty(name);
      if (val != null) return val;
      ResteasyConfiguration config = getResteasyConfiguration();
      if (config == null) return null;
      return config.getParameter(name);

   }

   public Cache getCache()
   {
      return cache;
   }

   protected Cache findCache()
   {
      Cache cache = (Cache)configurable.getProperty("skeleton.key.cache");
      if (cache != null) return cache;
      cache = getXmlCache();
      if (cache != null) return cache;
      return getDefaultCache();
   }

   protected Cache getDefaultCache()
   {
      EmbeddedCacheManager manager = new DefaultCacheManager();
      manager.defineConfiguration("custom-cache", new ConfigurationBuilder()
              .eviction().strategy(EvictionStrategy.NONE).maxEntries(1000)
              .build());
      return manager.getCache("custom-cache");
   }

   protected Cache getXmlCache()
   {
      String path = getConfigProperty(SKELETON_KEY_INFINISPAN_CONFIG_FILE);
      if (path == null) return null;

      String name = getConfigProperty(SKELETON_KEY_INFINISPAN_CACHE_NAME);
      if (name == null) throw new RuntimeException("need to specify skeleton.key.infinispan.cache.name");

      try
      {
         return new DefaultCacheManager(path).getCache(name);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

}
