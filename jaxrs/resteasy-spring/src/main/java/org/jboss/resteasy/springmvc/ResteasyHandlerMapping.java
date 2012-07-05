package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.NotFoundException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public class ResteasyHandlerMapping implements HandlerMapping, Ordered, InitializingBean
{
   private static Logger logger = Logger
           .getLogger(ResteasyHandlerMapping.class);

   private int order = Integer.MAX_VALUE;
   private SynchronousDispatcher dispatcher;

   private String prefix = "";
   private HandlerInterceptor[] interceptors;
   private boolean throwNotFound = false;

   public ResteasyHandlerMapping(SynchronousDispatcher dispatcher)
   {
      super();
      this.dispatcher = dispatcher;
   }

   public SynchronousDispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void setOrder(int order)
   {
      this.order = order;
   }

   public HandlerInterceptor[] getInterceptors()
   {
      return interceptors;
   }

   public void setInterceptors(HandlerInterceptor[] interceptors)
   {
      this.interceptors = interceptors;
   }

   public HandlerExecutionChain getHandler(HttpServletRequest request)
           throws Exception
   {
      ResteasyRequestWrapper requestWrapper = RequestUtil.getRequestWrapper(
              request, request.getMethod(), prefix);
      try
      {
         // NOTE: if invoker isn't found, RESTEasy throw NoReourceFoundFailure
         HttpRequest httpRequest = requestWrapper.getHttpRequest();
         if (!httpRequest.isInitial())
         {
            String message = httpRequest.getUri().getPath() + " is not initial request.  Its suspended and retried.  Aborting.";
            logger.error(message);
            requestWrapper.setError(500, message);
         }
         else
         {
            // todo not sure how to propagate aborted responses.  Man i hate this code Solomon!
            dispatcher.preprocess(httpRequest);
            requestWrapper.setInvoker(getInvoker(httpRequest));
         }
         return new HandlerExecutionChain(requestWrapper, interceptors);
      }
      catch (NotFoundException e)
      {
         if (throwNotFound)
         {
            throw e;
         }
         logger.error("Resource Not Found: " + e.getMessage(), e);
      }
      catch (Failure e)
      {
         logger.error("ResourceFailure: " + e.getMessage(), e);
         throw e;
      }
      return null;
   }

   private ResourceInvoker getInvoker(HttpRequest httpRequest)
   {
      if (dispatcher != null)
         return dispatcher.getInvoker(httpRequest);
      return null;
   }

   public int getOrder()
   {
      return order;
   }

   public boolean isThrowNotFound()
   {
      return throwNotFound;
   }

   public void setThrowNotFound(boolean throwNotFound)
   {
      this.throwNotFound = throwNotFound;
   }

   public String getPrefix()
   {
      return prefix;
   }

   public void setPrefix(String prefix)
   {
      this.prefix = prefix;
   }

   public void afterPropertiesSet() throws Exception
   {
      if (!throwNotFound && order == Integer.MAX_VALUE)
      {
         logger.info("ResteasyHandlerMapping has the default order and throwNotFound settings.  Consider adding explicit ordering to your HandlerMappings, with ResteasyHandlerMapping being last, and set throwNotFound = true.");
      }
   }
}
