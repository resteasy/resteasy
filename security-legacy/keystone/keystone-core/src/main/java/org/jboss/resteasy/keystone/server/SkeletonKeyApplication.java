package org.jboss.resteasy.keystone.server;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.keystone.core.i18n.LogMessages;
import org.jboss.resteasy.keystone.core.i18n.Messages;
import org.jboss.resteasy.keystone.model.Mappers;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyApplication
{
   public static final String SKELETON_KEY_INFINISPAN_CONFIG_FILE = "skeleton.key.infinispan.config.file";
   public static final String SKELETON_KEY_INFINISPAN_CACHE_NAME = "skeleton.key.infinispan.cache.name";
   Configurable configurable;
   protected Set<Object> singletons = new HashSet<Object>();

   protected PrivateKey privateKey;
   protected X509Certificate certificate;
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

      Mappers.registerContextResolver(confgurable);

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
      initKeyPair();

      singletons.add(new ServerTokenAuthFilter(tokenService));

   }

   protected void initKeyPair()
   {
      try
      {
         String keyStoreFile = getConfigProperty("skeleton.key.keyStoreFile");
         String keyStorePassword = getConfigProperty("skeleton.key.keyStorePassword");
         String alias = getConfigProperty("skeleton.key.alias");
         KeyStore keyStore = null;
         if (keyStoreFile == null)
         {
            String path = getConfigProperty("skeleton.key.keyStorePath");
            if (path == null)
            {
               LogMessages.LOGGER.warn(Messages.MESSAGES.noKeystoreProvided());
               return;
            }
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (is == null)
            {
               throw new RuntimeException(Messages.MESSAGES.keystorePathInvalid(path));
            }
            keyStore = KeyStore.getInstance("jks");
            keyStore.load(is, keyStorePassword.toCharArray());
            is.close();
         }
         else
         {
            keyStore = KeyStore.getInstance("jks");
            InputStream is = new FileInputStream(keyStoreFile);
            keyStore.load(is, keyStorePassword.toCharArray());
            is.close();
         }

         privateKey = (PrivateKey)keyStore.getKey(alias, keyStorePassword.toCharArray());
         if (privateKey == null) throw new RuntimeException(Messages.MESSAGES.privateKeyNull());
         certificate = (X509Certificate)keyStore.getCertificate(alias);
         if (certificate == null) throw new RuntimeException(Messages.MESSAGES.certificateNull());
         tokenService.setPrivateKey(privateKey);
         tokenService.setCertificate(certificate);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
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
      String val = (String)configurable.getConfiguration().getProperty(name);
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
      Cache cache = (Cache)configurable.getConfiguration().getProperty("skeleton.key.cache");
      if (cache != null) return cache;
      cache = getXmlCache();
      if (cache != null) return cache;
      return getDefaultCache();
   }

   protected Cache getDefaultCache()
   {
      GlobalConfiguration gconfig = new GlobalConfigurationBuilder()
       .globalJmxStatistics()
       .allowDuplicateDomains(true)
       .enable()
       .jmxDomain("custom-cache")
       .build();

      Configuration configuration = new ConfigurationBuilder()
          .eviction()
          .strategy(EvictionStrategy.NONE)
          .maxEntries(1000)
          .jmxStatistics().enable()
          .build();
      ConfigurationBuilder configBuilder = new ConfigurationBuilder().read(configuration);
      EmbeddedCacheManager manager = new DefaultCacheManager(gconfig, configuration);
      return manager.getCache("custom-cache");
   }

   protected Cache getXmlCache()
   {
      String path = getConfigProperty(SKELETON_KEY_INFINISPAN_CONFIG_FILE);
      if (path == null) return null;

      String name = getConfigProperty(SKELETON_KEY_INFINISPAN_CACHE_NAME);
      if (name == null) throw new RuntimeException(Messages.MESSAGES.needToSpecifyCacheName());
      
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
