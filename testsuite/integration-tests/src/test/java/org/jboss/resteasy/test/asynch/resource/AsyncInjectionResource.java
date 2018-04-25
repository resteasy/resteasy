package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/")
public class AsyncInjectionResource
{

   @Context
   AsyncInjectionContext resolvedContextField;
   @Context
   CompletionStage<AsyncInjectionContext> asyncContextField;

   AsyncInjectionContext resolvedContextConstructor;
   CompletionStage<AsyncInjectionContext> asyncContextConstructor;

   AsyncInjectionContext resolvedContextProperty;
   CompletionStage<AsyncInjectionContext> asyncContextProperty;

   public AsyncInjectionResource(@Context AsyncInjectionContext resolvedContextConstructor,
         @Context CompletionStage<AsyncInjectionContext> asyncContextConstructor)
   {
      this.resolvedContextConstructor = resolvedContextConstructor;
      this.asyncContextConstructor = asyncContextConstructor;
   }
   
   public CompletionStage<AsyncInjectionContext> getAsyncContextProperty()
   {
      return asyncContextProperty;
   }
   
   @Context
   public void setAsyncContextProperty(CompletionStage<AsyncInjectionContext> asyncContextProperty)
   {
      this.asyncContextProperty = asyncContextProperty;
   }
   
   public AsyncInjectionContext getResolvedContextProperty()
   {
      return resolvedContextProperty;
   }
   
   @Context
   public void setResolvedContextProperty(AsyncInjectionContext resolvedContextProperty)
   {
      this.resolvedContextProperty = resolvedContextProperty;
   }
   
   @GET
   public Response asyncInjectionPoints(@Context AsyncInjectionContext resolvedContextParam,
         @Context CompletionStage<AsyncInjectionContext> asyncContextParam)
         throws InterruptedException, ExecutionException
   {
      if (resolvedContextParam == null || resolvedContextParam.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context param").build();
      if (asyncContextParam == null || asyncContextParam.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context param").build();

      if (resolvedContextField == null || resolvedContextField.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context field").build();
      if (asyncContextField == null || asyncContextField.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context field").build();

      if (resolvedContextProperty == null || resolvedContextProperty.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context property").build();
      if (asyncContextProperty == null || asyncContextProperty.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context property").build();

      if (resolvedContextConstructor == null || resolvedContextConstructor.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context constructor").build();
      if (asyncContextConstructor == null || asyncContextConstructor.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context constructor").build();

      return Response.ok("resource").build();
   }

   @Path("/resolved")
   @GET
   public Response asyncInjectionResolved(@Context AsyncInjectionContext resolvedContextParam,
         @Context CompletionStage<AsyncInjectionContext> asyncContextParam,
         @Context HttpRequest request)
         throws InterruptedException, ExecutionException
   {
      if (resolvedContextParam == null || resolvedContextParam.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context param").build();
      if (asyncContextParam == null || asyncContextParam.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context param").build();

      if(request.getAsyncContext().isSuspended())
         return Response.serverError().entity("Suspended request").build();
      
      return Response.ok("resource").build();
   }

   @Path("/suspended")
   @GET
   public Response asyncInjectionSuspended(@AsyncInjectionContextAsyncSpecifier @Context AsyncInjectionContext resolvedContextParam,
         @AsyncInjectionContextAsyncSpecifier @Context CompletionStage<AsyncInjectionContext> asyncContextParam,
         @Context HttpRequest request)
         throws InterruptedException, ExecutionException
   {
      if (resolvedContextParam == null || resolvedContextParam.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context param").build();
      if (asyncContextParam == null || asyncContextParam.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context param").build();

      if(!request.getAsyncContext().isSuspended())
         return Response.serverError().entity("Non-suspended request").build();
      
      return Response.ok("resource").build();
   }
}
