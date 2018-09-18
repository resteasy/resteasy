package org.jboss.resteasy.core;

import javax.ws.rs.ext.ContextResolver;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContextResolverProxy implements ContextResolver
{
   private List<ContextResolver> resolvers;

   public Object getContext(Class type)
   {
      if (resolvers == null) return null;
      for (ContextResolver resolver : resolvers)
      {
         @SuppressWarnings(value = "unchecked")
         Object rtn = resolver.getContext(type);
         if (rtn != null) return rtn;
      }
      return null;
   }
}
