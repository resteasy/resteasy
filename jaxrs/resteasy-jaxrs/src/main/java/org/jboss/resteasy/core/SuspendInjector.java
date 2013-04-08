package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SuspendInjector implements ValueInjector
{
   private Suspend suspend;

   public SuspendInjector(Suspend suspend, Class type)
   {
      if (!type.equals(AsynchronousResponse.class))
         throw new IllegalArgumentException(type.getName() + " is not a valid injectable type for @Suspend");
      this.suspend = suspend;
   }

   public Object inject()
   {
      throw new IllegalStateException("You cannot inject into a form outside the scope of an HTTP request");
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      final ResteasyAsynchronousContext asynchronousContext = request.getAsyncContext();
      final AsyncResponse asynchronousResponse = asynchronousContext.suspend(suspend.value());
      return new AsynchronousResponse()
      {
         @Override
         public void setResponse(Response response)
         {
            asynchronousResponse.resume(response);
         }
      };
   }
}