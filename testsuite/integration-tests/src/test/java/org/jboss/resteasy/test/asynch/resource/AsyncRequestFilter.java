package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext;

public abstract class AsyncRequestFilter implements ContainerRequestFilter {

   private String name;
   
   public AsyncRequestFilter(String name)
   {
      this.name = name;
   }

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      PreMatchContainerRequestContext ctx = (PreMatchContainerRequestContext) requestContext;
      String action = ctx.getHeaderString(name);
      System.err.println("Filter request for "+name+" with action: "+action);
      if("sync-pass".equals(action)) {
         // do nothing
      }else if("sync-fail".equals(action)) {
         ctx.abortWith(Response.ok(name).build());
      }else if("async-pass".equals(action)) {
         ctx.suspend();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> ctx.resume());
      }else if("async-fail".equals(action)) {
         ctx.suspend();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> ctx.abortWith(Response.ok(name).build()));
      }
      System.err.println("Filter request for "+name+" with action: "+action+" done");
   }
   
}
