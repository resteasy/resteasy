package org.jboss.resteasy.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * This implementation of InjectionTarget is a wrapper that allows JAX-RS property injection
 * to be performed just after CDI injection.
 * 
 * @author Jozef Hartinger
 *
 */
public class JaxrsInjectionTarget<T> implements InjectionTarget<T>
{
   private InjectionTarget<T> delegate;
   private PropertyInjector propertyInjector;
   
   public JaxrsInjectionTarget(InjectionTarget<T> delegate, PropertyInjector propertyInjector)
   {
      this.delegate = delegate;
      this.propertyInjector = propertyInjector;
   }

   public void inject(T instance, CreationalContext<T> ctx)
   {
      delegate.inject(instance, ctx);

      HttpRequest request = ResteasyProviderFactory.getContextData(HttpRequest.class);
      HttpResponse response = ResteasyProviderFactory.getContextData(HttpResponse.class);

      if ((request != null) && (response != null))
      {
         propertyInjector.inject(request, response, instance);
      }
      else
      {
         propertyInjector.inject(instance);
      }
   }

   public void postConstruct(T instance)
   {
      delegate.postConstruct(instance);
   }

   public void preDestroy(T instance)
   {
      delegate.preDestroy(instance);
   }

   public void dispose(T instance)
   {
      delegate.dispose(instance);
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return delegate.getInjectionPoints();
   }

   public T produce(CreationalContext<T> ctx)
   {
      return delegate.produce(ctx);
   }
}
