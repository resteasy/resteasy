package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerResponseContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;

public abstract class AsyncResponseFilter implements ContainerResponseFilter {

   private String name;
   private String callbackException;
   private static final Logger LOG = Logger.getLogger(AsyncRequestFilter.class);

   public AsyncResponseFilter(final String name)
   {
      this.name = name;
   }

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
         throws IOException
   {
      // copy request filter callback values
      for (Entry<String, List<String>> entry : requestContext.getHeaders().entrySet())
      {
         if(entry.getKey().startsWith("RequestFilterCallback"))
            addValuesToContext(responseContext, entry);
      }
      responseContext.getHeaders().add("ResponseFilterCallback"+name, String.valueOf(callbackException));
      callbackException = null;

      SuspendableContainerResponseContext ctx = (SuspendableContainerResponseContext) responseContext;
      String action = requestContext.getHeaderString(name);
      LOG.error("Filter response for "+name+" with action: "+action);
      if("sync-pass".equals(action)) {
         // do nothing
      }else if("sync-fail".equals(action)) {
         ctx.setEntity(name);
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
         executor.submit(() -> {
            ctx.setEntity(name);
            ctx.resume();
         });
      }else if("async-fail-late".equals(action)) {
         ctx.suspend();
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
            ctx.setEntity(name);
            ctx.resume();
         });
      }else if("async-fail-instant".equals(action)) {
         ctx.suspend();
         ctx.setEntity(name);
         ctx.resume();
      }else if("sync-throw".equals(action)) {
         throw new AsyncFilterException("ouch");
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
            ctx.setEntity(name);
            ResteasyAsynchronousResponse resp = req.getAsyncContext().getAsyncResponse();
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
      LOG.error("Filter response for "+name+" with action: "+action+" done");
   }

   @SuppressWarnings("unchecked")
   private void addValuesToContext(ContainerResponseContext responseContext, Entry<String, List<String>> entry)
   {
      // cast required to disambiguate with Object... method
      responseContext.getHeaders().addAll(entry.getKey(), (List)entry.getValue());
   }
}
