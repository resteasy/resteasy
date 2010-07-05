package org.jboss.resteasy.star.messaging.integration;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.jboss.resteasy.star.messaging.BindingRegistry;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MapComponentRegistry implements BindingRegistry
{
   protected ConcurrentHashMap<String, Object> registry = new ConcurrentHashMap<String, Object>();

   @Override
   public Object lookup(String name)
   {
      return registry.get(name);
   }

   @Override
   public boolean bind(String name, Object obj)
   {
      return registry.putIfAbsent(name, obj) == null;
   }

   @Override
   public void unbind(String name)
   {
      registry.remove(name);
   }

   @Override
   public void close()
   {
   }
}
