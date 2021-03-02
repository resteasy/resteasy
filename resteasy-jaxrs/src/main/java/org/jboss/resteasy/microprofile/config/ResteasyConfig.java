package org.jboss.resteasy.microprofile.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyConfig
{
   private static final Method GET_CONFIG;
   private static final Method GET_OPTIONAL_VALUE;
   private static final Method GET_PROPERTY_NAMES;
   private Object config;

   public enum SOURCE
   {
      SYSTEM,
      ENV,
      SERVLET_CONTEXT
   }

   static {
      Method getConfig;
      Method getOptionalValue;
      Method getPropertyNames;
      try {
          final ClassLoader classLoader = getClassLoader();
          // First check if the API is present.
          final Class<?> configProvider = Class.forName("org.eclipse.microprofile.config.ConfigProvider", false, classLoader);
          getConfig = configProvider.getDeclaredMethod("getConfig", ClassLoader.class);
          final Class<?> clazz = Class.forName("org.eclipse.microprofile.config.Config", false, classLoader);
          getOptionalValue = clazz.getDeclaredMethod("getOptionalValue", String.class, Class.class);
          getPropertyNames = clazz.getDeclaredMethod("getPropertyNames");
          // Next check if the RESTEasy implementation is on the class path
          Class.forName("org.jboss.resteasy.microprofile.config.ServletConfigSourceImpl", false, classLoader);
      } catch (Throwable ignore) {
          getConfig = null;
          getOptionalValue = null;
          getPropertyNames = null;
      }
      GET_CONFIG = getConfig;
      GET_OPTIONAL_VALUE = getOptionalValue;
      GET_PROPERTY_NAMES = getPropertyNames;
   }

   public ResteasyConfig()
   {
      if (GET_CONFIG != null)
      {
         try
         {
            config = GET_CONFIG.invoke(null, getClassLoader());
         }
         catch (Exception e)
         {
            // Leave config == null.
         }
      }
   }

   @SuppressWarnings("unchecked")
   public String getValue(String propertyName)
   {
      if (config == null)
      {
         return null;
      }
      try
      {
         Optional<String> opt = (Optional<String>) GET_OPTIONAL_VALUE.invoke(config, propertyName, String.class);
         return opt.orElse(null);
      }
      catch (IllegalAccessException | InvocationTargetException e) {
         LogMessages.LOGGER.debugf(e, "Failed to invoke the configuration API method %s.", GET_OPTIONAL_VALUE);
         return null;
     }
   }

   public String getValue(String propertyName, SOURCE source)
   {
      return getValue(propertyName, source, null);
   }

   public String getValue(String propertyName, SOURCE source, String defaultValue)
   {
      if (System.getSecurityManager() == null)
      {
         return getValue0(propertyName, source, defaultValue);
      }
      else
      {
         String value = null;
         try {
            value = AccessController.doPrivileged(new PrivilegedExceptionAction<String>()
            {
               @Override
               public String run() throws Exception
               {
                  return getValue0(propertyName, source, defaultValue);
               }
            });
         }
         catch (PrivilegedActionException pae)
         {
            throw new RuntimeException(pae);
         }
         return value;
      }
   }

   @SuppressWarnings("unchecked")
   public String getValue0(String propertyName, SOURCE source, String defaultValue)
   {
      if (config == null)
      {
         switch (source) {
            case SYSTEM:
               return System.getProperty(propertyName, defaultValue);

            case ENV:
               String value = System.getenv(propertyName);
               return value != null ? value : defaultValue;

            case SERVLET_CONTEXT:
               ServletContext context = ResteasyProviderFactory.getContextData(ServletContext.class);
               if (context != null)
               {
                  value = context.getInitParameter(propertyName);
                  return value != null ? value : defaultValue;
               }
               else
               {
                  return defaultValue;
               }

            default:
               throw new RuntimeException(Messages.MESSAGES.unknownEnumerationValue(source.toString()));
         }
      }
      else
      {
         try
         {
            Optional<String> opt = (Optional<String>) GET_OPTIONAL_VALUE.invoke(config, propertyName, String.class);
            return opt.orElse(defaultValue);
         }
         catch (IllegalAccessException | InvocationTargetException e) {
            LogMessages.LOGGER.debugf(e, "Failed to invoke the configuration API method %s.", GET_OPTIONAL_VALUE);
            return defaultValue;
        }
      }
   }

   public Iterable<String> getPropertyNames()
   {
      return config == null ? null : getPropertyNames0();
   }

   @SuppressWarnings("unchecked")
   public Iterable<String> getPropertyNames0()
   {
      if (System.getSecurityManager() == null)
      {
         try
         {
            return (Iterable<String>) GET_PROPERTY_NAMES.invoke(config);
         }
         catch (IllegalAccessException | InvocationTargetException e) {
            LogMessages.LOGGER.debugf(e, "Failed to invoke the configuration API method %s.", GET_PROPERTY_NAMES);
            return null;
         }
      }
      else
      {
         Iterable<String> value = null;
         try {
            value = AccessController.doPrivileged(new PrivilegedExceptionAction<Iterable<String>>()
            {
               @Override
               public Iterable<String> run() throws Exception
               {
                  try
                  {
                     return (Iterable<String>) GET_PROPERTY_NAMES.invoke(config);
                  }
                  catch (IllegalAccessException | InvocationTargetException e) {
                     LogMessages.LOGGER.debugf(e, "Failed to invoke the configuration API method %s.", GET_PROPERTY_NAMES);
                     return null;
                  }
               }
            });
         }
         catch (PrivilegedActionException pae)
         {
            throw new RuntimeException(pae);
         }
         return value;
      }
   }

   private static ClassLoader getClassLoader()
   {
      if (System.getSecurityManager() == null)
      {
          final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
          return tccl != null ? tccl : ResteasyConfig.class.getClassLoader();
      }
      try
      {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>()
         {
            @Override
            public ClassLoader run() throws Exception
            {
               final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
               return tccl != null ? tccl : ResteasyConfig.class.getClassLoader();
            }
         });
      }
      catch (PrivilegedActionException e)
      {
         throw new RuntimeException(Messages.MESSAGES.unableToFindClassloader(ResteasyConfig.class.getName()));
      }
  }
}
