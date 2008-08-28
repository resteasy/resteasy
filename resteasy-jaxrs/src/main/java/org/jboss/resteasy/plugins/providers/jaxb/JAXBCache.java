/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.annotations.JAXBConfig;
import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.resteasy.core.LoggerCategories;
import org.slf4j.Logger;

/**
 * A JAXBCache.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public final class JAXBCache
{

   private static final Logger logger = LoggerCategories.getProviderLogger();

   /**
    * 
    */
   private static JAXBCache instance = new JAXBCache();

   /**
    *
    */
   private ConcurrentHashMap<Object, JAXBContext> contextCache = new ConcurrentHashMap<Object, JAXBContext>();

   /**
    * Create a new JAXBCache.
    */
   private JAXBCache()
   {

   }

   /**
    * FIXME Comment this
    * 
    * @return
    */
   public static JAXBCache instance()
   {
      return instance;
   }

   /**
    * FIXME Comment this
    * 
    * @param classes
    * @return
    */
   public JAXBContext getJAXBContext(Class<?>... classes)
   {
      return getJAXBContext(null, classes);
   }

   /**
    * FIXME Comment this
    * 
    * @param packageNames
    * @return
    */
   public JAXBContext getJAXBContext(String... packageNames)
   {
      String contextPath = buildContextPath(packageNames);
      logger.debug("Locating JAXBContext for packages: {}", contextPath);
      return getJAXBContext(contextPath, null);
   }

   /**
    * FIXME Comment this
    * 
    * @param packageNames
    * @return
    */
   public JAXBContext getJAXBContext(JAXBConfig config, Class<?>... classes)
   {
      if (useJAXBConfig(config))
      {
         return processJAXBConfig(config);
      }
      return getJAXBContext(classes, config);
   }

   /**
    * FIXME Comment this
    * 
    * @param config
    * @return
    */
   private boolean useJAXBConfig(JAXBConfig config)
   {
      if (config != null)
      {
         if (config.packages().length > 0 && config.packages()[0].length() > 0)
         {
            return true;
         }
         return false;
      }
      return false;
   }

   /**
    * FIXME Comment this
    * 
    * @param config
    * @return
    */
   private JAXBContext processJAXBConfig(JAXBConfig config)
   {
      String contextPath = buildContextPath(config.packages());
      logger.debug("Locating JAXBContext for package: {}", contextPath);
      return getJAXBContext(contextPath, config);
   }

   /**
    * FIXME Comment this
    * 
    * @param packageNames
    * @return
    */
   private String buildContextPath(String[] packageNames)
   {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < packageNames.length; i++)
      {
         b.append(packageNames[i]);
         if (i != (packageNames.length - 1))
         {
            b.append(":");
         }
      }
      return b.toString();
   }

   /**
    * FIXME Comment this
    * 
    * @param contextPath
    * @return
    */
   public JAXBContext getJAXBContext(String contextPath, JAXBConfig config)
   {
      JAXBContext context = contextCache.get(contextPath);
      if (context == null)
      {
         try
         {
//            if (config != null)
//            {
//               context = new JAXBContextWrapper(contextPath, config);
//            }
            context = JAXBContext.newInstance(contextPath);
         }
         catch (JAXBException e)
         {
            throw new ExceptionAdapter(e);
         }
         contextCache.putIfAbsent(contextPath, context);
      }
      logger.debug("Locating JAXBContext for package: {}", contextPath);
      return context;
   }

   /**
    * FIXME Comment this
    * 
    * @param classesToBeBound
    * @param config
    * @return
    */
   private JAXBContext getJAXBContext(Class<?>[] classesToBeBound, JAXBConfig config)
   {
      JAXBContext context = contextCache.get(classesToBeBound);
      if (context == null)
      {
         try
         {
//            if (config != null)
//            {
//               context = new JAXBContextWrapper(classesToBeBound, config);
//            }
            context = JAXBContext.newInstance(classesToBeBound);
         }
         catch (JAXBException e)
         {
            throw new ExceptionAdapter(e);
         }
         contextCache.putIfAbsent(classesToBeBound, context);
      }
      logger.debug("Locating JAXBContext for package: {}", classesToBeBound);
      return context;
   }

}
