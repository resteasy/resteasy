package org.jboss.resteasy.plugins.servlet;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.servlet.i18n.Messages;
import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@HandlesTypes({Application.class, Path.class, Provider.class})
public class ResteasyServletInitializer implements ServletContainerInitializer
{
   final static Set<String> ignoredPackages = new HashSet<String>();

   static
   {
      ignoredPackages.add(AsynchronousDispatcher.class.getPackage().getName());
   }

   @Override
   public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException
   {
      if (classes == null || classes.size() == 0) return;
      for (ServletRegistration reg : servletContext.getServletRegistrations().values())
      {
         if (reg.getInitParameter("javax.ws.rs.Application") != null)
         {
            return; // there's already a servlet mapping, do nothing
         }
      }

      Set<Class<?>> appClasses = new HashSet<Class<?>>();
      Set<Class<?>> providers = new HashSet<Class<?>>();
      Set<Class<?>> resources = new HashSet<Class<?>>();

      for (Class<?> clazz : classes)
      {
         if (ignoredPackages.contains(clazz.getPackage().getName())) continue;
         if (clazz.isAnnotationPresent(Path.class))
         {
            resources.add(clazz);
         }
         else if (clazz.isAnnotationPresent(Provider.class))
         {
            providers.add(clazz);
         }
         else
         {
            appClasses.add(clazz);
         }
      }
      if (appClasses.size() == 0 && resources.size() == 0) return;

      if (appClasses.size() == 0)
      {
         // todo make sure we can do this on all servlet containers
         //handleNoApplicationClass(providers, resources, servletContext);
         return;
      }

      for (Class<?> app : appClasses)
      {
         register(app, providers, resources, servletContext);
      }
   }

   protected void handleNoApplicationClass(Set<Class<?>> providers, Set<Class<?>> resources, ServletContext servletContext)
   {
      ServletRegistration defaultApp = null;
      for (ServletRegistration reg : servletContext.getServletRegistrations().values())
      {
         if (reg.getName().equals(Application.class.getName()))
         {
            defaultApp = reg;
         }
      }
      if (defaultApp == null) return;
      throw new NotImplementedYetException(Messages.MESSAGES.defaultApplicationNotImplemented());

   }


   protected void register(Class<?> applicationClass, Set<Class<?>> providers, Set<Class<?>> resources, ServletContext servletContext)
   {
      ApplicationPath path = applicationClass.getAnnotation(ApplicationPath.class);
      if (path == null)
      {
         // todo we don't support this yet, i'm not sure if partial deployments are supported in all servlet containers
         return;
      }
      ServletRegistration.Dynamic reg = servletContext.addServlet(applicationClass.getName(), HttpServlet30Dispatcher.class);
      reg.setLoadOnStartup(1);
      reg.setAsyncSupported(true);
      reg.setInitParameter("javax.ws.rs.Application", applicationClass.getName());

      if (path != null)
      {
         String mapping = path.value();
         if (!mapping.startsWith("/")) mapping = "/" + mapping;
         String prefix = mapping;
         if (!prefix.equals("/") && prefix.endsWith("/")) prefix = prefix.substring(0, prefix.length() - 1);
         if (!mapping.endsWith("/*")) 
         {
            if (mapping.endsWith("/")) mapping += "*";
            else mapping += "/*";
         }
         // resteasy.servlet.mapping.prefix
         reg.setInitParameter("resteasy.servlet.mapping.prefix", prefix);
         reg.addMapping(mapping);
      }

      if (resources.size() > 0)
      {
         StringBuilder builder = new StringBuilder();
         boolean first = true;
         for (Class resource : resources)
         {
            if (first)
            {
               first = false;
            }
            else
            {
               builder.append(",");
            }

            builder.append(resource.getName());
         }
         reg.setInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES, builder.toString());
      }
      if (providers.size() > 0)
      {
         StringBuilder builder = new StringBuilder();
         boolean first = true;
         for (Class provider : providers)
         {
            if (first)
            {
               first = false;
            }
            else
            {
               builder.append(",");
            }
            builder.append(provider.getName());
         }
         reg.setInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_PROVIDERS, builder.toString());
      }

   }
}
