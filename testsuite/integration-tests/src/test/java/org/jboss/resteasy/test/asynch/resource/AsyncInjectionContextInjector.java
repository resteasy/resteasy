package org.jboss.resteasy.test.asynch.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ContextInjector;

@Provider
public class AsyncInjectionContextInjector implements ContextInjector<CompletionStage<AsyncInjectionContext>>
{

   @Override
   public CompletionStage<AsyncInjectionContext> resolve(
         Class<? extends CompletionStage<AsyncInjectionContext>> rawType, Type genericType, Annotation[] annotations)
   {
      return CompletableFuture.completedFuture(new AsyncInjectionContext());
   }

}
