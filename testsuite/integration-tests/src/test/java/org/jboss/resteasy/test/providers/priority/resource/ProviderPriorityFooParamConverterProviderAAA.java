package org.jboss.resteasy.test.providers.priority.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProviderPriorityFooParamConverterProviderAAA implements ParamConverterProvider {

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      return (ParamConverter<T>) new ProviderPriorityFooParamConverter("AAA");
   }
}
