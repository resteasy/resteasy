package org.jboss.resteasy.test.asynch.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ContextInjector;

@Provider
public class AsyncInjectionBooleanInjector implements ContextInjector<CompletionStage<Boolean>, Boolean>
{

   @Override
   public CompletionStage<Boolean> resolve(
         Class<? extends CompletionStage<Boolean>> rawType, Type genericType, Annotation[] annotations)
   {
      for (Annotation annotation : annotations)
      {
         if(annotation.annotationType() == AsyncInjectionPrimitiveInjectorSpecifier.class) {
            AsyncInjectionPrimitiveInjectorSpecifier.Type value = ((AsyncInjectionPrimitiveInjectorSpecifier)annotation).value();
            switch(value) {
               case NO_RESULT:
                  return null;
               case NULL:
                  return CompletableFuture.completedFuture(null);
               case VALUE:
                  return CompletableFuture.completedFuture(true);
            }
            break;
         }
      }
      return CompletableFuture.completedFuture(true);
   }

}
