package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext;

public abstract class AsyncResponseFilter implements ContainerResponseFilter {

   private String name;
   
   public AsyncResponseFilter(String name)
   {
      this.name = name;
   }

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
         throws IOException
   {
      ContainerResponseContextImpl ctx = (ContainerResponseContextImpl) responseContext;
      String action = requestContext.getHeaderString(name);
      System.err.println("Filter response for "+name+" with action: "+action);
      if("sync-pass".equals(action)) {
         // do nothing
      }else if("sync-fail".equals(action)) {
         ctx.setEntity(name);
      }else if("async-pass".equals(action)) {
         ctx.suspend();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> ctx.resume());
      }else if("async-fail".equals(action)) {
         ctx.suspend();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(() -> {
            ctx.setEntity(name);
            ctx.resume();
         });
      }
      System.err.println("Filter response for "+name+" with action: "+action+" done");
   }
}
