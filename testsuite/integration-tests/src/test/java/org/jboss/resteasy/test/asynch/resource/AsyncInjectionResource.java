package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionPrimitiveInjectorSpecifier.Type;

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

   public AsyncInjectionResource(final @Context AsyncInjectionContext resolvedContextConstructor,
                                 final @Context CompletionStage<AsyncInjectionContext> asyncContextConstructor)
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

   @Path("/interface")
   @GET
   public Response asyncInjectionInterface(@Context AsyncInjectionContextInterface resolvedContextParam,
         @Context CompletionStage<AsyncInjectionContextInterface> asyncContextParam)
         throws InterruptedException, ExecutionException
   {
      if (resolvedContextParam == null || resolvedContextParam.foo() != 42)
         return Response.serverError().entity("Missing injected resolved context param").build();
      if (asyncContextParam == null || asyncContextParam.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Missing injected async context param").build();

      return Response.ok("resource").build();
   }

   @Path("/exception")
   @GET
   public Response asyncInjectionException(@AsyncInjectionContextErrorSpecifier @Context AsyncInjectionContext resolvedContextParam,
         @AsyncInjectionContextErrorSpecifier @Context CompletionStage<AsyncInjectionContext> asyncContextParam)
         throws InterruptedException, ExecutionException
   {
      return Response.serverError().entity("Should have thrown").build();
   }

   @Path("/exception-async")
   @GET
   public Response asyncInjectionExceptionAsync(@AsyncInjectionContextErrorSpecifier @AsyncInjectionContextAsyncSpecifier
         @Context AsyncInjectionContext resolvedContextParam,
         @AsyncInjectionContextErrorSpecifier @AsyncInjectionContextAsyncSpecifier
         @Context CompletionStage<AsyncInjectionContext> asyncContextParam)
         throws InterruptedException, ExecutionException
   {
      return Response.serverError().entity("Should have thrown").build();
   }

   @Path("/late")
   @GET
   public Response asyncInjectionLate(@Context ResourceContext resourceContext)
         throws InterruptedException, ExecutionException
   {
      AsyncInjectionResource2 resource = resourceContext.getResource(AsyncInjectionResource2.class);
      if(resource == null)
         return Response.serverError().entity("Resource should not have been null").build();
      if(resource.asyncContextField == null || resource.asyncContextField.toCompletableFuture().get().foo() != 42)
         return Response.serverError().entity("Context field problem").build();
      if(resource.resolvedContextField != null)
         return Response.serverError().entity("Resolved context field problem").build();
      if(resource.resolvedContextFieldAsync != null)
         return Response.serverError().entity("Resolved async context field problem").build();
      return Response.ok("resource").build();
   }

   @Path("/primitives")
   @GET
   public Response asyncInjectionPrimitives(@Context Boolean boolBoxedTrue,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Boolean boolBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Boolean boolBoxedNull2,
         @Context boolean bool,

         @Context Character charBoxedS,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Character charBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Character charBoxedNull2,
         @Context char c,

         @Context Byte byteBoxed42,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Byte byteBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Byte byteBoxedNull2,
         @Context byte b,

         @Context Short shortBoxed42,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Short shortBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Short shortBoxedNull2,
         @Context short s,

         @Context Integer intBoxed42,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Integer intBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Integer intBoxedNull2,
         @Context int i,

         @Context Long longBoxed42,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Long longBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Long longBoxedNull2,
         @Context long l,

         @Context Float floatBoxed42,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Float floatBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Float floatBoxedNull2,
         @Context float f,

         @Context Double doubleBoxed42,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NULL) Double doubleBoxedNull,
         @Context @AsyncInjectionPrimitiveInjectorSpecifier(Type.NO_RESULT) Double doubleBoxedNull2,
         @Context double d)
         throws InterruptedException, ExecutionException
   {
      if(boolBoxedTrue == null || !boolBoxedTrue.booleanValue())
         return Response.serverError().entity("Missing boxed boolean").build();
      if(boolBoxedNull != null)
         return Response.serverError().entity("Non-null boxed boolean").build();
      if(boolBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed boolean 2").build();
      if(!bool)
         return Response.serverError().entity("Missing boolean").build();

      if(charBoxedS == null || charBoxedS.charValue() != 's')
         return Response.serverError().entity("Missing boxed char").build();
      if(charBoxedNull != null)
         return Response.serverError().entity("Non-null boxed char").build();
      if(charBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed char 2").build();
      if(c != 's')
         return Response.serverError().entity("Missing char").build();

      if(byteBoxed42 == null || byteBoxed42.intValue() != 42)
         return Response.serverError().entity("Missing boxed byte").build();
      if(byteBoxedNull != null)
         return Response.serverError().entity("Non-null boxed byte").build();
      if(byteBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed byte 2").build();
      if(b != 42)
         return Response.serverError().entity("Missing byte").build();

      if(shortBoxed42 == null || shortBoxed42.intValue() != 42)
         return Response.serverError().entity("Missing boxed short").build();
      if(shortBoxedNull != null)
         return Response.serverError().entity("Non-null boxed short").build();
      if(shortBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed short 2").build();
      if(s != 42)
         return Response.serverError().entity("Missing short").build();

      if(intBoxed42 == null || intBoxed42.intValue() != 42)
         return Response.serverError().entity("Missing boxed int").build();
      if(intBoxedNull != null)
         return Response.serverError().entity("Non-null boxed int").build();
      if(intBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed int 2").build();
      if(i != 42)
         return Response.serverError().entity("Missing int").build();

      if(longBoxed42 == null || longBoxed42.intValue() != 42)
         return Response.serverError().entity("Missing boxed long").build();
      if(longBoxedNull != null)
         return Response.serverError().entity("Non-null boxed long").build();
      if(longBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed long 2").build();
      if(l != 42)
         return Response.serverError().entity("Missing long").build();

      if(floatBoxed42 == null || floatBoxed42.floatValue() != 4.2f)
         return Response.serverError().entity("Missing boxed float").build();
      if(floatBoxedNull != null)
         return Response.serverError().entity("Non-null boxed float").build();
      if(floatBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed float 2").build();
      if(f != 4.2f)
         return Response.serverError().entity("Missing float").build();

      if(doubleBoxed42 == null || doubleBoxed42.doubleValue() != 4.2)
         return Response.serverError().entity("Missing boxed double").build();
      if(doubleBoxedNull != null)
         return Response.serverError().entity("Non-null boxed double").build();
      if(doubleBoxedNull2 != null)
         return Response.serverError().entity("Non-null boxed double 2").build();
      if(d != 4.2)
         return Response.serverError().entity("Missing double").build();

      return Response.ok("resource").build();
   }
}
