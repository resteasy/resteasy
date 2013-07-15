package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchronousResponseInjector implements ValueInjector
{
   long timeout = -1;
   TimeUnit unit = null;

   public AsynchronousResponseInjector()
   {
   }

   @Override
   public Object inject()
   {
      throw new IllegalStateException("You cannot inject AsynchronousResponse outside the scope of an HTTP request");
   }

   @Override
   public Object inject(HttpRequest request, HttpResponse response)
   {
      ResteasyAsynchronousResponse asynchronousResponse = null;
      if (timeout == -1)
      {
         asynchronousResponse = request.getAsyncContext().suspend();
      }
      else
      {
         asynchronousResponse = request.getAsyncContext().suspend(timeout, unit);
      }
      ResourceMethodInvoker invoker =  (ResourceMethodInvoker)request.getAttribute(ResourceMethodInvoker.class.getName());
      invoker.initializeAsync(asynchronousResponse);

      return asynchronousResponse;
   }
}
