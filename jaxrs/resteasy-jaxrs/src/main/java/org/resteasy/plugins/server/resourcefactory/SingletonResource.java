package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResourceReference;

/**
 * VERY simple implementation that just returns the instance the SingleResource was created with
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SingletonResource implements ResourceReference
{
   private Object obj;
   private boolean initialized = false;

   public SingletonResource(Object obj)
   {
      this.obj = obj;
   }

   public ResourceFactory getFactory(InjectorFactory factory)
   {
      if (!initialized)
      {
         initialized = true;
         factory.createPropertyInjector(obj.getClass()).inject(obj);
      }

      return new ResourceFactory()
      {
         public Object createResource(HttpRequest input, HttpResponse response)
         {
            return obj;
         }
      };
   }

   public Class<?> getScannableClass()
   {
      return obj.getClass();
   }
}
