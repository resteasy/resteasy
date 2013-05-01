package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.plugins.providers.StringTextStar;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.util.FindAnnotation;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public class CustomValueInjectorTest
{

   @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Hello
   {
      String value();
   }

   @Path("")
   public static class HelloResource
   {
      @GET
      @Produces("text/plain")
      public String get(@Hello("world") String hello)
      {
         return hello;
      }
   }

   @Test
   public void testCustomInjectorFactory() throws Exception
   {
      InMemoryClientExecutor executor = new InMemoryClientExecutor(initializeDispatcher());
      executor.getRegistry().addPerRequestResource(HelloResource.class);

      Object result = new ClientRequest("/", executor).get().getEntity(String.class);
      Assert.assertEquals("world", result);
   }

   private SynchronousDispatcher initializeDispatcher()
   {
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      providerFactory.registerProvider(StringTextStar.class);

      // use @Provider annotation to register a custom ValueInjector!!!
      providerFactory.registerProvider(MyInjectorFactoryImpl.class);

      return new SynchronousDispatcher(providerFactory);
   }

   public static class MyInjectorFactoryImpl extends InjectorFactoryImpl
   {
      @Override
      public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
                                                    Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
      {
         final Hello hello = FindAnnotation.findAnnotation(annotations, Hello.class);
         if (hello == null)
         {
            return super.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, factory);
         }
         else
         {
            return new ValueInjector()
            {
               public Object inject(HttpRequest request, HttpResponse response)
               {
                  return hello.value();
               }

               public Object inject()
               {
                  return hello.value();
               }
            };
         }
      }

      @Override
      public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory)
      {
         final Hello hello = FindAnnotation.findAnnotation(parameter.getAnnotations(), Hello.class);
         if (hello == null)
         {
            return super.createParameterExtractor(parameter, providerFactory);
         }
         else
         {
            return new ValueInjector()
            {
               public Object inject(HttpRequest request, HttpResponse response)
               {
                  return hello.value();
               }

               public Object inject()
               {
                  return hello.value();
               }
            };
         }
      }
   }

   ;

}
