/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * A JAXBCache.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public final class JAXBCache
{

   private static JAXBCache instance = new JAXBCache();

   /**
    * 
    */
   private ConcurrentHashMap<Class<?>, JAXBContext> contextCache =
      new ConcurrentHashMap<Class<?>, JAXBContext>();
   

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
    * Lookup.  Will create context if it doesn't exist.  
    * Might be useful for prepopulating contextCache.
    *
    * @param clazz
    * @return
    * @throws JAXBException
    */
   protected JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException
   {
      JAXBContext context = contextCache.get(clazz);
      if (context == null)
      {
         context = JAXBContext.newInstance(clazz);
         contextCache.putIfAbsent(clazz, context);
      }
      return context;
   }
}
