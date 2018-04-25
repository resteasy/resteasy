package org.jboss.resteasy.test.asynch.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
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
      boolean async = false;
      for (Annotation annotation : annotations)
      {
         if(annotation.annotationType() == AsyncInjectionContextAsyncSpecifier.class)
         {
            async = true;
            break;
         }
      }
      if(async)
      {
         CompletableFuture<AsyncInjectionContext> ret = new CompletableFuture<>();
         new Thread(() -> {
            try
            {
               Thread.sleep(1000);
            } catch (InterruptedException e)
            {
               throw new RuntimeException(e);
            }
            ret.complete(new AsyncInjectionContext());
         }).start();
         return ret;
      }
      return CompletableFuture.completedFuture(new AsyncInjectionContext());
   }

}
