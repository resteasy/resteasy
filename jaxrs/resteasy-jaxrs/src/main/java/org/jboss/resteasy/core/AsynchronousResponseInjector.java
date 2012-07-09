package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.Suspend;
import javax.ws.rs.core.AsynchronousResponse;
import java.lang.reflect.AccessibleObject;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchronousResponseInjector implements ValueInjector
{
   long timeout = -1;
   TimeUnit unit = null;

   public AsynchronousResponseInjector(AccessibleObject target)
   {
      Suspend suspend = target.getAnnotation(Suspend.class);
      if (suspend != null)
      {
         this.timeout = suspend.timeOut();
         this.unit = suspend.timeUnit();
      }
   }

   @Override
   public Object inject()
   {
      throw new IllegalStateException("You cannot inject AsynchronousResponse outside the scope of an HTTP request");
   }

   @Override
   public Object inject(HttpRequest request, HttpResponse response)
   {
      AsynchronousResponse asynchronousResponse = null;
      if (timeout == -1)
      {
         asynchronousResponse = request.getExecutionContext().suspend();
      }
      else
      {
         asynchronousResponse = request.getExecutionContext().suspend(timeout, unit);
      }
      return asynchronousResponse;
   }
}
