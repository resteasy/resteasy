package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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
   public CompletionStage<Object> inject(boolean unwrapAsync)
   {
      throw new IllegalStateException(Messages.MESSAGES.cannotInjectAsynchronousResponse());
   }

   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
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

      return CompletableFuture.completedFuture(asynchronousResponse);
   }
}
