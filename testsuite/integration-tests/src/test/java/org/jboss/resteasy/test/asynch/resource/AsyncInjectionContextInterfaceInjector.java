package org.jboss.resteasy.test.asynch.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ContextInjector;

@Provider
public class AsyncInjectionContextInterfaceInjector implements ContextInjector<CompletionStage<AsyncInjectionContextInterface>, AsyncInjectionContextInterface>
{

   @Override
   public CompletionStage<AsyncInjectionContextInterface> resolve(
         Class<? extends CompletionStage<AsyncInjectionContextInterface>> rawType, Type genericType, Annotation[] annotations)
   {
      return CompletableFuture.completedFuture(new AsyncInjectionContext());
   }

}
