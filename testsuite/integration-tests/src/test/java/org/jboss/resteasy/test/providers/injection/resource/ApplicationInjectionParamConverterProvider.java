package org.jboss.resteasy.test.providers.injection.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationInjectionParamConverterProvider implements ParamConverterProvider {
   
   @Context ApplicationInjectionApplicationParent application;
   
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      return new ApplicationInjectionParamConverter<T>(application);
   }
   
   public static class ApplicationInjectionParamConverter<T> implements ParamConverter<T> {

      private ApplicationInjectionApplicationParent application;
      
      public ApplicationInjectionParamConverter(ApplicationInjectionApplicationParent application) {
         this.application = application;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public T fromString(String value) {
         return (T) (getClass() + ":" + application.getName());
      }

      @Override
      public String toString(T value) {
         return getClass() + ":" + application.getName() + application.getName();
      }
   }
}
