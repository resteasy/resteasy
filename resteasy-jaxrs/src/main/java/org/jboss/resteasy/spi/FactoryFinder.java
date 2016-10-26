package org.jboss.resteasy.spi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

final class FactoryFinder {

   private static final Logger LOGGER = Logger.getLogger(FactoryFinder.class.getName());

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
       // try to find services in CLASSPATH
       try {
           InputStream is;
           if (classLoader == null) {
               is = ClassLoader.getSystemResourceAsStream(serviceId);
           } else {
               is = classLoader.getResourceAsStream(serviceId);
           }

           if (is != null) {
               BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));

               String factoryClassName = rd.readLine();
               rd.close();

               if (factoryClassName != null && !"".equals(factoryClassName)) {
                   return newInstance(factoryClassName, classLoader);
               }
           }
       } catch (Exception ex) {
           LOGGER.log(Level.FINER, "Failed to load service " + factoryId + " from " + serviceId, ex);
       }


       // try to read from $java.home/lib/jaxrs.properties
       try {
           String javah = System.getProperty("java.home");
           String configFile = javah + File.separator
                   + "lib" + File.separator + "jaxrs.properties";
           File f = new File(configFile);
           if (f.exists()) {
               Properties props = new Properties();
               props.load(new FileInputStream(f));
               String factoryClassName = props.getProperty(factoryId);
               return newInstance(factoryClassName, classLoader);
           }
       } catch (Exception ex) {
           LOGGER.log(Level.FINER, "Failed to load service " + factoryId
                   + " from $java.home/lib/jaxrs.properties", ex);
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

       if (fallbackClassName == null) {
           throw new ClassNotFoundException(
                   "Provider for " + factoryId + " cannot be found", null);
       }

       return newInstance(fallbackClassName, classLoader);
   }
}
