/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the logging categories used by the RESTEasy.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class LoggerCategories
{

   /**
    * A logging category used by entity providers.
    */
   public static final String PROVIDER = "org.jboss.resteasy.plugins.providers";

   /**
    * A logging category used by the server-related classes.
    */
   public static final String SERVER = "org.jboss.resteasy.plugins.server";

   /**
    * A logging category used by the mock-related classes.
    */
   public static final String MOCK = "org.jboss.resteasy.mock";

   /**
    * A logging category used by the core classes of RESTEasy.
    */
   public static final String CORE = "org.jboss.resteasy.core";

   /**
    * A logging category used by the classes which implement the JAX-RS spec.
    */
   public static final String IMPL = "org.jboss.resteasy.specimpl";
   
   /**
    * A logging category used by RESTEasy delegates.
    */
   public static final String DELEGATES = "org.jboss.resteasy.plugins.delegates";

   private LoggerCategories()
   {

   }

   /**
    * Returns the logger for the specified logger name.
    * 
    * @param name the logger name
    * @return the logger.
    */
   public static Logger getLogger(String name)
   {
      return LoggerFactory.getLogger(name);
   }
   
   /**
    * FIXME Comment this
    * 
    * @param clazz
    * @return
    */
   public static Logger getLogger(Class<?> clazz)
   {
      return LoggerFactory.getLogger(clazz);
   }

   /**
    * Returns the Logger instance that is associated with the "provider" 
    * category.
    * 
    * @return
    */
   public static Logger getProviderLogger()
   {
      return getLogger(PROVIDER);
   }

   /**
    * Returns the Logger instance that is associated with the "core" 
    * category.
    * 
    * @return
    */
   public static Logger getCoreLogger()
   {
      return getLogger(CORE);
   }
   
   /**
    * Returns the Logger instance that is associated with the "mock" 
    * category.
    * 
    * @return
    */
   public static Logger getMockLogger()
   {
      return getLogger(MOCK);
   }
   
   /**
    * Returns the Logger instance that is associated with the "server" 
    * category.
    * 
    * @return
    */
   public static Logger getServerLogger()
   {
      return getLogger(SERVER);
   }
   
   /**
    * Returns the Logger instance that is associated with the "specimpl" 
    * category.
    * 
    * @return
    */
   public static Logger getSpecImplLogger()
   {
      return getLogger(IMPL);
   }
   
   /**
    * Returns the Logger instance that is associated with the "delegates" 
    * category.
    * 
    * @return
    */
   public static Logger getDelegatesLogger()
   {
      return getLogger(DELEGATES);
   }
}
