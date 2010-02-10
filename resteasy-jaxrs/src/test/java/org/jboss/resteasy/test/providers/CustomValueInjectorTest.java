package org.jboss.resteasy.test.providers;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.specimpl.HttpHeadersImpl;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.junit.Assert;
import org.junit.Test;

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
   public void testCustomInjectorFactory() throws SecurityException, NoSuchMethodException, Failure, URISyntaxException
   {
      ResteasyProviderFactory instance = new ResteasyProviderFactory();
      instance.setInjectorFactory(new InjectorFactoryImpl(instance)
      {
         @Override
         public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget,
               Class type, Type genericType, Annotation[] annotations)
         {
            final Hello hello = FindAnnotation.findAnnotation(annotations, Hello.class);
            if( hello == null )
               return super.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations);
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
      });

      
      final Method method = HelloResource.class.getDeclaredMethod("get", String.class);
      final MethodInjector methodInjector = instance.getInjectorFactory()
            .createMethodInjector(HelloResource.class, method);
      
      final MockHttpRequest request = MockHttpRequest.get("");
      HttpHeadersImpl impl = (HttpHeadersImpl) request.getHttpHeaders();
      impl.setMediaType(MediaType.TEXT_PLAIN_TYPE);
      final Object[] results = methodInjector.injectArguments(request, new MockHttpResponse());
      Assert.assertEquals(1, results.length);
      Assert.assertEquals("world", results[0]);
   }
}
