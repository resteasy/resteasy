package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.AsyncResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchronousResponseInjector implements ValueInjector
{
   long timeout = -1;
   TimeUnit unit = null;

   public AsynchronousResponseInjector(Suspended suspend)
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
      AsyncResponse asynchronousResponse = null;
      if (timeout == -1)
      {
         asynchronousResponse = request.getAsyncContext().suspend();
      }
      else
      {
         asynchronousResponse = request.getAsyncContext().suspend(timeout, unit);
      }
      return asynchronousResponse;
   }
}
