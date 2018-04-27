package org.jboss.resteasy.test.asynch.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ContextInjector;

@Provider
public class AsyncInjectionContextInjector implements ContextInjector<CompletionStage<AsyncInjectionContext>, AsyncInjectionContext>
{

   @Override
   public CompletionStage<AsyncInjectionContext> resolve(
         Class<? extends CompletionStage<AsyncInjectionContext>> rawType, Type genericType, Annotation[] annotations)
   {
      boolean async = false;
      boolean error = false;
      for (Annotation annotation : annotations)
      {
         if(annotation.annotationType() == AsyncInjectionContextAsyncSpecifier.class)
         {
            async = true;
            break;
         }
         if(annotation.annotationType() == AsyncInjectionContextErrorSpecifier.class)
         {
            error = true;
            break;
         }
      }
      if(async)
      {
         CompletableFuture<AsyncInjectionContext> ret = new CompletableFuture<>();
         boolean finalError = error;
         new Thread(() -> {
            try
            {
               Thread.sleep(1000);
            } catch (InterruptedException e)
            {
               throw new RuntimeException(e);
            }
            if(finalError)
               ret.completeExceptionally(new AsyncInjectionException("Async exception"));
            else
               ret.complete(new AsyncInjectionContext());
         }).start();
         return ret;
      }
      if(error)
      {
         CompletableFuture<AsyncInjectionContext> ret = new CompletableFuture<>();
         ret.completeExceptionally(new AsyncInjectionException("Async exception"));
         return ret;
      }
      return CompletableFuture.completedFuture(new AsyncInjectionContext());
   }

}
