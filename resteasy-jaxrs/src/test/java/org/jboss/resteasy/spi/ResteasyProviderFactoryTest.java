package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import org.junit.Assert;
import org.junit.Test;

public class ResteasyProviderFactoryTest {

   /**
    * This unit test exposes the bug of not comparing {@link ExceptionMapper}s before
    * registering to the {@link ResteasyProviderFactory}, but the latest registration overrides
    * the previous ones. [RESTEASY-1739]
    */
   @Test
   public void testExceptionMappersPriority() {
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      providerFactory.addExceptionMapper(ExceptionMapper1.class, false);
      providerFactory.addExceptionMapper(ExceptionMapper2.class, false);
      providerFactory.addExceptionMapper(ExceptionMapper3.class, false);
      Assert.assertFalse(providerFactory.getExceptionMapper(IllegalArgumentException.class).getClass().isAssignableFrom(ExceptionMapper1.class));
      Assert.assertTrue(providerFactory.getExceptionMapper(IllegalArgumentException.class).getClass().isAssignableFrom(ExceptionMapper2.class));
      Assert.assertFalse(providerFactory.getExceptionMapper(IllegalArgumentException.class).getClass().isAssignableFrom(ExceptionMapper3.class));
   }

   /**
    * This unit test exposes the bug of not sorting {@link ParamConverterProvider}s using the required parameters ({@link Priority} and builtIn attribute),
    * but the insertion order applies when looking for a {@link ParamConverter}. [RESTEASY-1739]
    */
   @Test
   public void testParamConverterProviderPriority() {
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      providerFactory.registerProvider(MapParamConverterProvider1.class, false);
      providerFactory.registerProvider(MapParamConverterProvider2.class, false);
      providerFactory.registerProvider(MapParamConverterProvider3.class, false);
      Assert.assertFalse(providerFactory.getParamConverter(Map.class, null, null).getClass().isAssignableFrom(MapParamConverter1.class));
      Assert.assertTrue(providerFactory.getParamConverter(Map.class, null, null).getClass().isAssignableFrom(MapParamConverter2.class));
      Assert.assertFalse(providerFactory.getParamConverter(Map.class, null, null).getClass().isAssignableFrom(MapParamConverter3.class));
   }

   public static class ExceptionMapper1 implements ExceptionMapper<IllegalArgumentException> {
      @Override
      public Response toResponse(IllegalArgumentException exception) {
         return Response.ok().build();
      }
   }

   @Priority(10)
   public static class ExceptionMapper2 implements ExceptionMapper<IllegalArgumentException> {
      @Override
      public Response toResponse(IllegalArgumentException exception) {
         return Response.ok().build();
      }
   }

   @Priority(100)
   public static class ExceptionMapper3 implements ExceptionMapper<IllegalArgumentException> {
      @Override
      public Response toResponse(IllegalArgumentException exception) {
         return Response.ok().build();
      }
   }

   public static class MapParamConverterProvider1 implements ParamConverterProvider {

      public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
         return (ParamConverter<T>) new MapParamConverter1();
      }

   }

   @Priority(5)
   public static class MapParamConverterProvider2 implements ParamConverterProvider {

      public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
         return (ParamConverter<T>) new MapParamConverter2();
      }
   }

   @Priority(200)
   public static class MapParamConverterProvider3 implements ParamConverterProvider {

      public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
         return (ParamConverter<T>) new MapParamConverter3();
      }
   }

   private static abstract class MapParamConverter implements ParamConverter<Map> {
      public Map fromString(String value) {
         return Collections.emptyMap();
      }

      public String toString(Map value) {
         return value.toString();
      }
   }

   public static class MapParamConverter1 extends MapParamConverter {
   }

   public static class MapParamConverter2 extends MapParamConverter {
   }

   public static class MapParamConverter3 extends MapParamConverter {
   }
}
