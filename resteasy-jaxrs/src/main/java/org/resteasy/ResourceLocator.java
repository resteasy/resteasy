package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.MethodInjector;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResourceReference;
import org.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocator implements ResourceFactory, ResourceReference
{
   protected MethodInjector injector;
   protected ResourceFactory factory;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected PathParamIndex index;

   public ResourceLocator(MethodInjector injector, ResourceFactory factory, ResteasyProviderFactory providerFactory, Method method, PathParamIndex index)
   {
      this.injector = injector;
      this.factory = factory;
      this.providerFactory = providerFactory;
      this.method = method;
      this.index = index;
   }

   public Object createResource(HttpRequest input, HttpResponse response)
   {
      index.populateUriInfoTemplateParams(input);
      Object resource = factory.createResource(input, response);
      Object[] args = injector.injectArguments(input, response);
      try
      {
         return method.invoke(resource, args);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getCause());
      }
   }

   public Class<?> getScannableClass()
   {
      return method.getReturnType();
   }

   public ResourceFactory getFactory(InjectorFactory factory)
   {
      return this;
   }
}
