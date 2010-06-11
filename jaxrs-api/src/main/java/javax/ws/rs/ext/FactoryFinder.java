/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * FactoryFinder.java
 *
 * Created on November 16, 2007, 3:14 PM
 *
 */
package javax.ws.rs.ext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class FactoryFinder
{

   static ClassLoader getContextClassLoader()
   {
      return AccessController.doPrivileged(
              new PrivilegedAction<ClassLoader>()
              {
                 public ClassLoader run()
                 {
                    ClassLoader cl = null;
                    try
                    {
                       cl = Thread.currentThread().getContextClassLoader();
                    }
                    catch (SecurityException ex) { }
                    return cl;
                 }
              });
   }

   /**
    * Creates an instance of the specified class using the specified
    * <code>ClassLoader</code> object.
    *
    * @throws ClassNotFoundException if the given class could not be found
    *                                or could not be instantiated
    */
   private static Object newInstance(String className,
                                     ClassLoader classLoader) throws ClassNotFoundException
   {
      try
      {
         Class spiClass;
         if (classLoader == null)
         {
            spiClass = Class.forName(className);
         }
         else
         {
            try
            {
               spiClass = Class.forName(className, false, classLoader);
            }
            catch (ClassNotFoundException ex)
            {
               spiClass = Class.forName(className);
            }
         }
         return spiClass.newInstance();
      }
      catch (ClassNotFoundException x)
      {
         throw x;
      }
      catch (Exception x)
      {
         throw new ClassNotFoundException(
                 "Provider " + className + " could not be instantiated: " + x,
                 x);
      }
   }

   /**
    * Finds the implementation <code>Class</code> object for the given
    * factory name, or if that fails, finds the <code>Class</code> object
    * for the given fallback class name. The arguments supplied MUST be
    * used in order. If using the first argument is successful, the second
    * one will not be used.
    * <p/>
    * This method is package private so that this code can be shared.
    *
    * @param factoryId         the name of the factory to find, which is
    *                          a system property
    * @param fallbackClassName the implementation class name, which is
    *                          to be used only if nothing else
    *                          is found; <code>null</code> to indicate that
    *                          there is no fallback class name
    * @return the <code>Class</code> object of the specified message factory;
    *         may not be <code>null</code>
    * @throws WebServiceException if there is an error
    */
   static Object find(String factoryId, String fallbackClassName) throws ClassNotFoundException
   {
      ClassLoader classLoader = getContextClassLoader();

      String serviceId = "META-INF/services/" + factoryId;
      // try to find services in CLASSPATH
      try
      {
         InputStream is;
         if (classLoader == null)
         {
            is = ClassLoader.getSystemResourceAsStream(serviceId);
         }
         else
         {
            is = classLoader.getResourceAsStream(serviceId);
         }

         if (is != null)
         {
            BufferedReader rd =
                    new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String factoryClassName = rd.readLine();
            rd.close();

            if (factoryClassName != null &&
                    !"".equals(factoryClassName))
            {
               try
               {
                  return newInstance(factoryClassName, classLoader);
               }
               catch (ClassNotFoundException e)
               {
                  URL url = classLoader.getResource(serviceId);

                  throw new ClassNotFoundException("Could not find from factory file" + url, e);
               }
            }
         }
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }


      // try to read from $java.home/lib/jaxrs.properties
      try
      {
         String javah = System.getProperty("java.home");
         String configFile = javah + File.separator +
                 "lib" + File.separator + "jaxrs.properties";
         File f = new File(configFile);
         if (f.exists())
         {
            Properties props = new Properties();
            props.load(new FileInputStream(f));
            String factoryClassName = props.getProperty(factoryId);
            return newInstance(factoryClassName, classLoader);
         }
      }
      catch (Exception ex)
      {
      }


      // Use the system property
      try
      {
         String systemProp =
                 System.getProperty(factoryId);
         if (systemProp != null)
         {
            return newInstance(systemProp, classLoader);
         }
      }
      catch (SecurityException se)
      {
      }

      if (fallbackClassName == null)
      {
         throw new ClassNotFoundException(
                 "Provider for " + factoryId + " cannot be found", null);
      }

      return newInstance(fallbackClassName, classLoader);
   }
}
