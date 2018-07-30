package org.jboss.resteasy.spi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

final class FactoryFinder {

   private static final Logger LOGGER = Logger.getLogger(FactoryFinder.class.getName());
   private static final String RESTEASY_JAXRS_API_MODULE = "org.jboss.resteasy.resteasy-jaxrs-api";

   private FactoryFinder() {
       // prevents instantiation
   }

   static ClassLoader getContextClassLoader() {
       return AccessController.doPrivileged(
               new PrivilegedAction<ClassLoader>() {

                   @Override
                   public ClassLoader run() {
                       ClassLoader cl = null;
                       try {
                           cl = Thread.currentThread().getContextClassLoader();
                       } catch (SecurityException ex) {
                           LOGGER.log(
                                   Level.WARNING,
                                   "Unable to get context classloader instance.",
                                   ex);
                       }
                       return cl;
                   }
               });
   }

   /**
    * Creates an instance of the specified class using the specified
    * {@code ClassLoader} object.
    *
    * @param className   name of the class to be instantiated.
    * @param classLoader class loader to be used.
    * @return instance of the specified class.
    * @throws ClassNotFoundException if the given class could not be found
    *                                or could not be instantiated.
    */
   private static Object newInstance(final String className,
                                     final ClassLoader classLoader) throws ClassNotFoundException {
       try {
           Class spiClass;
           if (classLoader == null) {
               spiClass = Class.forName(className);
           } else {
               try {
                   spiClass = Class.forName(className, false, classLoader);
               } catch (ClassNotFoundException ex) {
                   LOGGER.log(
                           Level.FINE,
                           "Unable to load provider class " + className
                                   + " using custom classloader " + classLoader.getClass().getName()
                                   + " trying again with current classloader.",
                           ex);
                   spiClass = Class.forName(className);
               }
           }
           return spiClass.newInstance();
       } catch (ClassNotFoundException x) {
           throw x;
       } catch (Exception x) {
           throw new ClassNotFoundException("Provider " + className + " could not be instantiated: " + x, x);
       }
   }

   /**
    * Finds the implementation {@code Class} object for the given
    * factory name, or if that fails, finds the {@code Class} object
    * for the given fallback class name. The arguments supplied MUST be
    * used in order. If using the first argument is successful, the second
    * one will not be used.
    * <p>
    * This method is package private so that this code can be shared.
    * </p>
    *
    * @param factoryId         the name of the factory to find, which is
    *                          a system property.
    * @param fallbackClassName the implementation class name, which is
    *                          to be used only if nothing else.
    *                          is found; {@code null} to indicate that
    *                          there is no fallback class name.
    * @return the {@code Class} object of the specified message factory;
    *         may not be {@code null}.
    * @throws ClassNotFoundException if there is an error.
    */
   static Object find(final String factoryId, final String fallbackClassName) throws ClassNotFoundException {
       ClassLoader classLoader = getContextClassLoader();

       String serviceId = "META-INF/services/" + factoryId;
       BufferedReader rd = null;
       InputStream is = null;
       // try to find services in CLASSPATH
       try {
           if (classLoader == null) {
               is = ClassLoader.getSystemResourceAsStream(serviceId);
           } else {
               is = classLoader.getResourceAsStream(serviceId);
           }

           if (is != null) {
               rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));

               String factoryClassName = rd.readLine();

               if (factoryClassName != null && !"".equals(factoryClassName)) {
                   return newInstance(factoryClassName, classLoader);
               }
           }
       } catch (Exception ex) {
           LOGGER.log(Level.FINER, "Failed to load service " + factoryId + " from " + serviceId, ex);
       } finally {
           try {
	       if (rd != null)
		   rd.close();

               if (is != null)
                   is.close();
	   } catch (IOException ex) {
	       LOGGER.log(Level.FINER, "Failed to close  BufferedReader/InputStream.", ex);
	   }
	}

       FileInputStream fis = null;
       // try to read from $java.home/lib/jaxrs.properties
       try {
           String javah = System.getProperty("java.home");
           String configFile = javah + File.separator
                   + "lib" + File.separator + "jaxrs.properties";
           File f = new File(configFile);
           if (f.exists()) {
               Properties props = new Properties();
               fis = new FileInputStream(f);
               props.load(fis);
               String factoryClassName = props.getProperty(factoryId);
               return newInstance(factoryClassName, classLoader);
           }
       } catch (Exception ex) {
           LOGGER.log(Level.FINER, "Failed to load service " + factoryId
                   + " from $java.home/lib/jaxrs.properties", ex);
       } finally {
           try {
	       if (fis != null)
		   fis.close();
	   } catch (IOException ex) {
	       LOGGER.log(Level.FINER, "Failed to close  FileInputStream.", ex);
	   }
	}

       // Use the system property
       try {
           String systemProp = System.getProperty(factoryId);
           if (systemProp != null) {
               return newInstance(systemProp, classLoader);
           }
       } catch (SecurityException se) {
           LOGGER.log(Level.FINER, "Failed to load service " + factoryId
                   + " from a system property", se);
       }

       ClassLoader moduleClassLoader = getModuleClassLoader();
       rd = null;
       is = null;
       if (moduleClassLoader != null) {
          try {
             is = moduleClassLoader.getResourceAsStream(serviceId);
         
             if( is!=null ) {
                 rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
         
                 String factoryClassName = rd.readLine();

                 if (factoryClassName != null &&
                     ! "".equals(factoryClassName)) {
                     return newInstance(factoryClassName, moduleClassLoader);
                 }
             }
         } catch( Exception ex ) {
         } finally {
           try {
	       if (rd != null)
		   rd.close();

               if (is != null)
                   is.close();
	   } catch (IOException ex) {
               LOGGER.log(Level.FINER, "Failed to close  BufferedReader/InputStream.", ex);
	   }
	 }
       }

       if (fallbackClassName == null) {
           throw new ClassNotFoundException(
                   "Provider for " + factoryId + " cannot be found", null);
       }

       return newInstance(fallbackClassName, classLoader);
   }

   private static ClassLoader getModuleClassLoader() {
      try {
          final Class<?> moduleClass = Class.forName("org.jboss.modules.Module");
          final Class<?> moduleIdentifierClass = Class.forName("org.jboss.modules.ModuleIdentifier");
          final Class<?> moduleLoaderClass = Class.forName("org.jboss.modules.ModuleLoader");
          final Object moduleLoader;
          final SecurityManager sm = System.getSecurityManager();
          if (sm == null) {
              moduleLoader = moduleClass.getMethod("getBootModuleLoader").invoke(null);
          } else {
              try {
                  moduleLoader = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                      public Object run() throws Exception {
                          return moduleClass.getMethod("getBootModuleLoader").invoke(null);
                      }
                  });
              } catch (PrivilegedActionException pae) {
                  throw pae.getException();
              }
          }
          Object moduleIdentifier = moduleIdentifierClass.getMethod("create", String.class).invoke(null, RESTEASY_JAXRS_API_MODULE);
          Object module = moduleLoaderClass.getMethod("loadModule", moduleIdentifierClass).invoke(moduleLoader, moduleIdentifier);
          return (ClassLoader)moduleClass.getMethod("getClassLoader").invoke(module);
       } catch (ClassNotFoundException e) {
          //ignore, JBoss Modules might not be available at all
           return null;
       } catch (RuntimeException e) {
          throw e;
       } catch (Exception e) {
          throw new RuntimeException(e);
       }
   }

}
