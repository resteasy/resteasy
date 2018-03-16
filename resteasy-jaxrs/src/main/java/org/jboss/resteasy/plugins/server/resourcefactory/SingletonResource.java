package org.jboss.resteasy.plugins.server.resourcefactory;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;

/**
 * VERY simple implementation that just returns the instance the SingleResource was created with
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SingletonResource implements ResourceFactory
{
   private final Object obj;
   private final ResourceClass resourceClass;

   @Deprecated
   public SingletonResource(Object obj)
   {
      this.obj = obj;
      this.resourceClass = ResourceBuilder.rootResourceFromAnnotations(obj.getClass());
   }
   
   public SingletonResource(Object obj, ResourceClass resourceClass)
   {
      this.obj = obj;
      this.resourceClass = resourceClass;
   }

   public void registered(ResteasyProviderFactory factory)
   {
      factory.getInjectorFactory().createPropertyInjector(resourceClass, factory).inject(obj);
   }

   public Object createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory)
   {
      return obj;
   }

   public void unregistered()
   {
   }

   public Class<?> getScannableClass()
   {
      return obj.getClass();
   }

   public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
   {
   }
}
