package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormInjector implements ValueInjector
{
   private Class type;
   private PropertyInjector injector;

   public FormInjector(Class type, ResteasyProviderFactory factory)
   {
      this.type = type;
      injector = new PropertyInjectorImpl(type, factory);

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
         throw new InternalServerErrorException("Failed to instantiate @Form class", e.getCause());
      }
      catch (IllegalAccessException e)
      {
         throw new InternalServerErrorException("Failed to instantiate @Form class", e);
      }
      injector.inject(request, response, target);
      return target;
   }
}
