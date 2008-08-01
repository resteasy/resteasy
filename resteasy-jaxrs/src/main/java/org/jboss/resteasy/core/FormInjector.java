package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormInjector implements ValueInjector
{
   private Class type;
   private ResteasyProviderFactory factory;
   private PathParamIndex index;
   private PropertyInjector injector;

   public FormInjector(Class type, ResteasyProviderFactory factory, PathParamIndex index)
   {
      this.type = type;
      this.factory = factory;
      this.index = index;
      injector = new PropertyInjectorImpl(type, index, factory);

   }

   public Object inject()
   {
      throw new IllegalStateException("You cannot inject into a form outside the scope of an HTTP request");
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      Object target = null;
      try
      {
         target = type.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e.getCause());
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      injector.inject(request, response, target);
      return target;
   }
}
