package org.jboss.resteasy.plugins.server.vertx;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VertxRegistry implements Registry
{

   private final Registry delegate;
   private final ResourceBuilder resourceBuilder;

   public VertxRegistry(Registry delegate, ResourceBuilder resourceBuilder)
   {
      this.delegate = delegate;
      this.resourceBuilder = resourceBuilder;
   }


   public void addPerInstanceResource(Class<?> clazz)
   {
      delegate.addResourceFactory(new VertxResourceFactory(new POJOResourceFactory(resourceBuilder, clazz)));
   }

   public void addPerInstanceResource(Class<?> clazz, String basePath)
   {
      delegate.addResourceFactory(new VertxResourceFactory(new POJOResourceFactory(resourceBuilder, clazz)), basePath);
   }

   public void addPerInstanceResource(ResourceClass resourceClass)
   {
      delegate.addResourceFactory(new VertxResourceFactory(new POJOResourceFactory(resourceBuilder, resourceClass)));
   }

   public void addPerInstanceResource(ResourceClass resourceClass, String basePath)
   {
      delegate.addResourceFactory(new VertxResourceFactory(new POJOResourceFactory(resourceBuilder, resourceClass)), basePath);
   }

   @Override
   public void addPerRequestResource(Class<?> clazz)
   {
      delegate.addPerRequestResource(clazz);
   }

   @Override
   public void addPerRequestResource(Class<?> clazz, String basePath)
   {
      delegate.addPerRequestResource(clazz, basePath);
   }

   @Override
   public void addSingletonResource(Object singleton)
   {
      delegate.addSingletonResource(singleton);
   }

   @Override
   public void addSingletonResource(Object singleton, String basePath)
   {
      delegate.addSingletonResource(singleton, basePath);
   }

   @Override
   public void addJndiResource(String jndiName)
   {
      delegate.addJndiResource(jndiName);
   }

   @Override
   public void addJndiResource(String jndiName, String basePath)
   {
      delegate.addJndiResource(jndiName, basePath);
   }

   @Override
   public void addResourceFactory(ResourceFactory ref)
   {
      delegate.addResourceFactory(ref);
   }

   @Override
   public void addResourceFactory(ResourceFactory ref, String basePath)
   {
      delegate.addResourceFactory(ref, basePath);
   }

   @Override
   public void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz)
   {
      delegate.addResourceFactory(ref, base, clazz);
   }

   @Override
   public void addResourceFactory(ResourceFactory ref, String base, Class<?>[] classes)
   {
      delegate.addResourceFactory(ref, base, classes);
   }

   @Override
   public void removeRegistrations(Class<?> clazz)
   {
      delegate.removeRegistrations(clazz);
   }

   @Override
   public void removeRegistrations(Class<?> clazz, String base)
   {
      delegate.removeRegistrations(clazz, base);
   }

   @Override
   public int getSize()
   {
      return delegate.getSize();
   }

   @Override
   public ResourceInvoker getResourceInvoker(HttpRequest request)
   {
      return delegate.getResourceInvoker(request);
   }

   @Override
   public void addResourceFactory(ResourceFactory rf, String base, ResourceClass resourceClass)
   {
      delegate.addResourceFactory(rf, base, resourceClass);
   }

   @Override
   public void removeRegistrations(ResourceClass resourceClass)
   {
      delegate.removeRegistrations(resourceClass);
   }

   @Override
   public void addPerRequestResource(ResourceClass clazz)
   {
      delegate.addPerRequestResource(clazz);
   }

   @Override
   public void addPerRequestResource(ResourceClass clazz, String basePath)
   {
      delegate.addPerRequestResource(clazz, basePath);
   }

   @Override
   public void addSingletonResource(Object singleton, ResourceClass resourceClass)
   {
      delegate.addSingletonResource(singleton, resourceClass);
   }

   @Override
   public void addSingletonResource(Object singleton, ResourceClass resourceClass, String basePath)
   {
      delegate.addSingletonResource(singleton, resourceClass, basePath);
   }

   @Override
   public void addJndiResource(String jndiName, ResourceClass resourceClass)
   {
      delegate.addJndiResource(jndiName, resourceClass);
   }

   @Override
   public void addJndiResource(String jndiName, ResourceClass resourceClass, String basePath)
   {
      delegate.addJndiResource(jndiName, resourceClass, basePath);
   }

   @Override
   public void checkAmbiguousUri()
   {
      //no-op
   }
}
