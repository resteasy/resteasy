package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Path("/")
public class AsyncInjectionResource2
{

   @Context
   AsyncInjectionContext resolvedContextField;
   @Context
   CompletionStage<AsyncInjectionContext> asyncContextField;

   @Context
   @AsyncInjectionContextAsyncSpecifier
   AsyncInjectionContext resolvedContextFieldAsync;

   public AsyncInjectionResource2()
   {
   }
}
