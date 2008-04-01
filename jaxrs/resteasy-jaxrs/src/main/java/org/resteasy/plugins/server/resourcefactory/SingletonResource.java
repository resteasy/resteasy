package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.ResourceFactory;

/**
 * VERY simple implementation that just returns the instance the SingleResource was created with
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SingletonResource implements ResourceFactory
{
   private Object obj;
   private boolean initialized = false;

   public SingletonResource(Object obj)
   {
      this.obj = obj;
   }

   public void registered(InjectorFactory factory)
   {
      factory.createPropertyInjector(obj.getClass()).inject(obj);
   }

   public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
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
}
