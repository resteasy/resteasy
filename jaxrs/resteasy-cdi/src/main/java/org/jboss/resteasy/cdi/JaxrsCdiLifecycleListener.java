package org.jboss.resteasy.cdi;

import org.jboss.resteasy.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Stuart Douglas
 */
public class JaxrsCdiLifecycleListener implements ServletContextListener, ServletRequestListener {

   private final Logger log = Logger.getLogger(JaxrsCdiLifecycleListener.class);


   private static final ThreadLocal<List<CreationalContext<?>>> currentObjects = new ThreadLocal<List<CreationalContext<?>>>();
   private static final ThreadLocal<List<CreationalContext<?>>> currentApplicationScopedObjects = new ThreadLocal<List<CreationalContext<?>>>();

   private final List<CreationalContext<?>> applicationScopedObjects = Collections.synchronizedList(new ArrayList<CreationalContext<?>>());

   @Override
   public void contextInitialized(ServletContextEvent sce) {
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce) {
      for (CreationalContext<?> ctx : applicationScopedObjects) {
         ctx.release();
      }
      applicationScopedObjects.clear();
   }

   @Override
   public void requestDestroyed(ServletRequestEvent sre) {
      List<CreationalContext<?>> current = currentObjects.get();
      if (current != null) {
         currentObjects.remove();
         for (CreationalContext<?> ctx : current) {
            try {
               ctx.release();
            } catch (Exception e) {
               log.error("Failed to destroy CDI bean", e);
            }
         }
      }
      List<CreationalContext<?>> app = currentApplicationScopedObjects.get();
      if (app != null) {
         currentApplicationScopedObjects.remove();
         applicationScopedObjects.addAll(app);
      }
   }

   public static void addObject(final CreationalContext<?> ctx) {
      List<CreationalContext<?>> current = currentObjects.get();
      if (current == null) {
         currentObjects.set(current = new ArrayList<CreationalContext<?>>());
      }
      current.add(ctx);
   }

   public static void addApplicationScopedObject(final CreationalContext<?> ctx) {
      List<CreationalContext<?>> current = currentApplicationScopedObjects.get();
      if (current == null) {
         currentApplicationScopedObjects.set(current = new ArrayList<CreationalContext<?>>());
      }
      current.add(ctx);
   }

   @Override
   public void requestInitialized(ServletRequestEvent sre) {
   }
}
