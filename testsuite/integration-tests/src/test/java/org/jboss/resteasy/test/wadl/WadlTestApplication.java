package org.jboss.resteasy.test.wadl;

import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlWriter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Provider
@ApplicationPath("/")
public class WadlTestApplication extends Application {
   public static Set<Class<?>> classes = new HashSet<Class<?>>();
   public static Set<Object> singletons = new HashSet<Object>();

   /**
    * Load resources from classes.txt file from deployment
    *
    * @return Array of class names.
    */
   public static String[] getClassesFromDeployment(String name) {
      String resource = name + ".txt";
      String stripped = resource.startsWith("/") ?
            resource.substring(1) : resource;

      InputStream stream = null;
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader != null) {
         stream = classLoader.getResourceAsStream(stripped);
      }
      if (stream == null) {
         stream = TestApplication.class.getResourceAsStream(resource);
      }
      if (stream == null) {
         stream = TestApplication.class.getClassLoader().getResourceAsStream(stripped);
      }
      if (stream == null) {
         return new String[0];
      }
      return convertStreamToString(stream).split(",");
   }

   /**
    * Convert input stream to String
    *
    * @param is Input stream
    * @return string
    */
   private static String convertStreamToString(final java.io.InputStream is) {
      java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
   }

   /**
    * @see javax.ws.rs.core.Application#getClasses()
    */
   @Override
   public Set<Class<?>> getClasses() {
      if (classes.isEmpty()) {
         for (String clazz : getClassesFromDeployment("classes")) {
            if (!clazz.isEmpty()) {
               try {
                  classes.add(Class.forName(clazz));
               } catch (ClassNotFoundException e) {
                  throw new RuntimeException("Class " + clazz + " not found.", e);
               }
            }
         }
      }
      return classes;
   }

   /**
    * @see javax.ws.rs.core.Application#getSingletons()
    */
   @Override
   public Set<Object> getSingletons() {
      if (singletons.isEmpty()) {
         for (String clazz : getClassesFromDeployment("singletons")) {
            if (!clazz.isEmpty()) {
               try {
                  singletons.add(Class.forName(clazz).newInstance());
               } catch (Exception e) {
                  throw new RuntimeException("Class " + clazz + " not found.", e);
               }
            }
         }
         ResteasyWadlDefaultResource defaultResource = new ResteasyWadlDefaultResource();
         ResteasyWadlWriter.ResteasyWadlGrammar wadlGrammar = new ResteasyWadlWriter.ResteasyWadlGrammar();
         wadlGrammar.enableSchemaGeneration();
         defaultResource.getWadlWriter().setWadlGrammar(wadlGrammar);
         singletons.add(defaultResource);
      }
      return singletons;
   }
}
