package org.jboss.resteasy.plugins.guice;

import com.google.inject.Module;
import com.google.inject.Stage;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

public class GuiceResteasyBootstrapServletContextListener extends ResteasyBootstrap implements ServletContextListener
{
   private final static Logger logger = Logger.getLogger(GuiceResteasyBootstrapServletContextListener.class);

   public void contextInitialized(final ServletContextEvent event)
   {
      super.contextInitialized(event);
      final ServletContext context = event.getServletContext();
      final Registry registry = (Registry) context.getAttribute(Registry.class.getName());
      final ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context.getAttribute(ResteasyProviderFactory.class.getName());
      final ModuleProcessor processor = new ModuleProcessor(registry, providerFactory);
      final List<Module> modules = getModules(context);
      final Stage stage = getStage(context);
      final Injector injector = (stage == null) ? processor.process(modules) : processor.process(stage, modules);
      // Set context attribute
      context.setAttribute(Injector.class.getName(), injector);
      // if (stage == null)
      // {
      //    processor.process(modules);
      // }
      // else
      // {
      //    processor.process(stage, modules);
      // }
   }

   private Stage getStage(ServletContext context)
   {
      final String stageAsString = context.getInitParameter("resteasy.guice.stage");
      if (stageAsString == null)
      {
         return null;
      }
      try
      {
         return Stage.valueOf(stageAsString.trim());
      }
      catch (IllegalArgumentException e)
      {
         throw new RuntimeException("Injector stage is not defined properly. " + stageAsString + " is wrong value." +
                 " Possible values are PRODUCTION, DEVELOPMENT, TOOL.");
      }
   }

   protected List<Module> getModules(final ServletContext context)
   {
      final List<Module> result = new ArrayList<Module>();
      final String modulesString = context.getInitParameter("resteasy.guice.modules");
      if (modulesString != null)
      {
         final String[] moduleStrings = modulesString.trim().split(",");
         for (final String moduleString : moduleStrings)
         {
            try
            {
               logger.info("found module: {0}", moduleString);
               final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(moduleString.trim());
               final Module module = (Module) clazz.newInstance();
               result.add(module);
            }
            catch (ClassNotFoundException e)
            {
               throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
               throw new RuntimeException(e);
            }
            catch (InstantiationException e)
            {
               throw new RuntimeException(e);
            }
         }

      }
      return result;
   }

   public void contextDestroyed(final ServletContextEvent event)
   {
   }
}
