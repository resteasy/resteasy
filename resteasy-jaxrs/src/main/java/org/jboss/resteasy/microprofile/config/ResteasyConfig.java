package org.jboss.resteasy.microprofile.config;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.servlet.ServletContext;

import org.eclipse.microprofile.config.Config;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyConfig
{
   private Config config;

   public enum SOURCE
   {
      SYSTEM,
      ENV,
      SERVLET_CONTEXT
   }

   public ResteasyConfig()
   {
      try
      {
         Class.forName("org.eclipse.microprofile.config.spi.ConfigSource");
         Class.forName("org.jboss.resteasy.microprofile.config.ServletConfigSourceImpl");
         config = ResteasyConfigProvider.getConfig();
      }
      catch (Throwable e)
      {
         // Leave config == null.
      }
   }

   public String getValue(String propertyName)
   {
      return config == null ? null : config.getOptionalValue(propertyName, String.class).orElse(null);
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
         return config.getOptionalValue(propertyName, String.class).orElse(defaultValue);
      }
   }

   public Iterable<String> getPropertyNames()
   {
      return config == null ? null : getPropertyNames0();
   }

   public Iterable<String> getPropertyNames0()
   {
      if (System.getSecurityManager() == null)
      {
         return config.getPropertyNames();
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
                  return config.getPropertyNames();
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
}
