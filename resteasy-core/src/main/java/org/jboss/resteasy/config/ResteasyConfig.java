package org.jboss.resteasy.config;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.ServiceLoader;

import org.jboss.resteasy.microprofile.config.ResteasyConfigProvider;

public interface ResteasyConfig {
   ResteasyConfig instance = ResteasyConfigSpi.getConfigInstance();

   <T> Optional<T> getOptionalValue(String propertyKey, Class<T> propertyType);


}

class ResteasyConfigSpi {
   static ResteasyConfig getConfigInstance() {
      ServiceLoader<ResteasyConfig> serviceLoader = ServiceLoader.load(ResteasyConfig.class);
      ResteasyConfig cfg = serviceLoader.iterator().hasNext() ? serviceLoader.iterator().next() : null;
      if (cfg == null)
      {
         serviceLoader = ServiceLoader.load(ResteasyConfig.class, AccessController
               .doPrivileged((PrivilegedAction<ClassLoader>) () -> ResteasyConfig.class.getClassLoader()));
         cfg = serviceLoader.iterator().hasNext() ? serviceLoader.iterator().next() : null;
      }
      if (cfg == null)
      {
         cfg = new ResteasyConfigProvider();
      }
      return cfg;
   }
}