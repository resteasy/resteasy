package org.jboss.resteasy.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassAsset;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import io.undertow.Undertow;

/**
 * Test runner to use instead of Arquillian to run tests in the IDE
 *
 * If HV is giving you trouble, add this line to AbstractValidatorContextResolver.getValidatorFactory() in the catch block:
 * config.messageInterpolator(new ParameterMessageInterpolator());
 *
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class UndertowTestRunner extends BlockJUnit4ClassRunner
{

   private Set<Class<?>> classes = new HashSet<>();
   private Class<?> application;

   public UndertowTestRunner(final Class<?> klass) throws InitializationError
   {
      super(klass);
      loadDeployment(klass);
   }

   private void loadDeployment(Class<?> klass)
   {
      for (Method method : klass.getDeclaredMethods())
      {
         if(Modifier.isStatic(method.getModifiers())
               && method.isAnnotationPresent(Deployment.class))
         {
            try
            {
               Archive<?> archive = (Archive<?>) method.invoke(null);
               for (Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
               {
                  Asset asset = entry.getValue().getAsset();
                  if(asset instanceof ClassAsset) {
                     Class<?> classAsset = ((ClassAsset)asset).getSource();
                     if(classAsset.isAnnotationPresent(Provider.class)) {
                        if(Application.class.isAssignableFrom(classAsset))
                           application = classAsset;
                        else if(classAsset.isAnnotationPresent(Path.class)
                              && !classAsset.isInterface())
                           classes.add(classAsset);
                     }
                  }
               }
               return;
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      throw new RuntimeException("Could not find method annotated with @Deployment to guess class names");
   }

   @Override
   public void run(RunNotifier notifier)
   {
      UndertowJaxrsServer server = new UndertowJaxrsServer().start(Undertow.builder()
            .addHttpListener(8080, "localhost"));
      Application app;
      if(application != null) {
         try
         {
            app = (Application) application.newInstance();
         } catch (InstantiationException | IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
      }else
         app = new Application() {
            @Override
            public Set<Class<?>> getClasses()
            {
               return classes;
            }
         };
      server.deploy(app, super.getTestClass().getJavaClass().getSimpleName());
      try
      {
         super.run(notifier);
      }
      finally
      {
         server.stop();
      }

   }

}
