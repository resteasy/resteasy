package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.resteasy.util.DelegateClassLoader;

public final class ResteasyConfigProvider
{
   public static Config getConfig() {
      return ConfigProvider.getConfig(new DelegateClassLoader(ResteasyConfigProvider.class.getClassLoader(), Thread.currentThread().getContextClassLoader()));
   }
}
