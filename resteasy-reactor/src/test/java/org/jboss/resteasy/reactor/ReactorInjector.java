package org.jboss.resteasy.reactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ContextInjector;
import reactor.core.publisher.Mono;


@Provider
public class ReactorInjector implements ContextInjector<Mono<Integer>, Integer>{

   @Override
   public Mono<Integer> resolve(Class<? extends Mono<Integer>> rawType, Type genericType,
         Annotation[] annotations) {
      boolean async = false;
      for (Annotation annotation : annotations)
      {
         if(annotation.annotationType() == Async.class)
            async = true;
      }
      if(!async)
         return Mono.just(42);
      return Mono.create(emitter -> {
         new Thread(() -> {
            try
            {
               Thread.sleep(1000);
            } catch (InterruptedException e)
            {
               emitter.error(e);
               return;
            }
            emitter.success(42);
         }).start();
      });
   }

}
