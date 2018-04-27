package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

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
