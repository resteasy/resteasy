package org.jboss.resteasy.jsapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ResourceMethodRegistry;

/**
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class MetaDataService
{

   private static final long serialVersionUID = -1985015444704126795L;

   private ResourceMethodRegistry registry;

   public MetaDataService(ResourceMethodRegistry registry)
   {
      this.registry = registry;
   }

   public List<MethodMetaData> getMethodMetaData()
   {
      ArrayList<MethodMetaData> results = new ArrayList<MethodMetaData>();
      for (Entry<String, List<ResourceInvoker>> entry : registry.getRoot()
            .getBounded().entrySet())
      {
         List<ResourceInvoker> invokers = entry.getValue();
         for (ResourceInvoker invoker : invokers)
         {
            if (invoker instanceof ResourceMethod)
            {
               results.add(new MethodMetaData(entry.getKey(),
                     (ResourceMethod) invoker));
            } else
            {
               // TODO: fix this?
            }
         }
      }
      return results;
   }
}
