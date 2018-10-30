package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerRequestContext;
import org.jboss.resteasy.spi.HttpRequest;

public abstract class AsyncRequestFilter implements ContainerRequestFilter {

   private String name;
   private String callbackException;
   private static final Logger LOG = Logger.getLogger(AsyncRequestFilter.class);

   public AsyncRequestFilter(final String name)
   {
      this.name = name;
   }

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      requestContext.getHeaders().add("RequestFilterCallback"+name, String.valueOf(callbackException));
      callbackException = null;

      SuspendableContainerRequestContext ctx = (SuspendableContainerRequestContext) requestContext;
      String action = ctx.getHeaderString(name);
      LOG.error("Filter request for "+name+" with action: "+action);
      if("sync-pass".equals(action)) {
         // do nothing
      }else if("sync-fail".equals(action)) {
         ctx.abortWith(Response.ok(name).build());
      }else if("async-pass".equals(action)) {
         ctx.suspend();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> ctx.resume());
      }else if("async-pass-instant".equals(action)) {
         ctx.suspend();
         ctx.resume();
      }else if("async-fail".equals(action)) {
         ctx.suspend();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> ctx.abortWith(Response.ok(name).build()));
      }else if("async-fail-instant".equals(action)) {
         ctx.suspend();
         ctx.abortWith(Response.ok(name).build());
      }else if("async-throw-late".equals(action)) {
         ctx.suspend();
         HttpRequest req = ResteasyContext.getContextData(HttpRequest.class);
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> {
            try
            {
               Thread.sleep(2000);
            } catch (InterruptedException e)
            {
               // TODO Auto-generated catch block
               LOG.error("Error:", e);
            }
            AsyncResponse resp = req.getAsyncContext().getAsyncResponse();
            resp.register((CompletionCallback) (t) -> {
               if(callbackException != null)
                  throw new RuntimeException("Callback called twice");
               callbackException = Objects.toString(t);
            });
            if("true".equals(req.getHttpHeaders().getHeaderString("UseExceptionMapper")))
               ctx.resume(new AsyncFilterException("ouch"));
            else
               ctx.resume(new Throwable("ouch"));
         });
      }
      LOG.error("Filter request for "+name+" with action: "+action+" done");
   }

}
