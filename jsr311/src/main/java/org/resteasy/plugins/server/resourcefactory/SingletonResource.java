package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.HttpInput;
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

   public SingletonResource(Object obj)
   {
      this.obj = obj;
   }

   public Object createResource(HttpInput input)
   {
      return obj;
   }

   public Class<?> getScannableClass()
   {
      return obj.getClass();
   }
}
