package org.jboss.resteasy.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.Failure;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/
public class ResteasyHandlerMapping implements HandlerMapping, Ordered
{

   private int order = -1;
   private ResourceMethodRegistry registry;
   private String prefix = "";
   private HandlerInterceptor[] interceptors;

   public ResteasyHandlerMapping(ResourceMethodRegistry registry)
   {
      super();
      this.registry = registry;
   }

   public void setRegistry(ResourceMethodRegistry registry)
   {
      this.registry = registry;
   }

   public void setOrder(int order)
   {
      this.order = order;
   }

   public String getPrefix()
   {
      return prefix;
   }

   public HandlerInterceptor[] getInterceptors()
   {
      return interceptors;
   }

   public void setInterceptors(HandlerInterceptor[] interceptors)
   {
      this.interceptors = interceptors;
   }

   public ResourceMethodRegistry getRegistry()
   {
      return registry;
   }

   public void setPrefix(String prefix)
   {
      this.prefix = prefix;
   }

   public HandlerExecutionChain getHandler(HttpServletRequest request)
         throws Exception
   {
      try
      {
         ResteasyRequestWrapper responseWrapper = RequestUtil
               .getRequestWrapper(request, request.getMethod(), prefix);

         // TODO: remove null response requirement? The "response" parameter
         // isn't currently used in the registry
         ResourceInvoker invoker = registry.getResourceInvoker(responseWrapper
               .getHttpRequest(), null);

         if (invoker == null)
         {
            // if we don't have a JAX-RS invoker, let Spring MVC handle the
            // request.
            return null;
         }

         responseWrapper.setInvoker(invoker);
         return new HandlerExecutionChain(responseWrapper, interceptors);
      }
      catch (Failure e)
      {
         // TODO: proper handling?
         // handleFailure(in, response, e);
         // logger.info(e.getMessage());
         return null;
      }
   }

   public int getOrder()
   {
      return order;
   }

}
