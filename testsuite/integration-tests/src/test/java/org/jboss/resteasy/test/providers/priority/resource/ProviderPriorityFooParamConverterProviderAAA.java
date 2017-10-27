package org.jboss.resteasy.test.providers.priority.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class ProviderPriorityFooParamConverterProviderAAA implements ParamConverterProvider {

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      return (ParamConverter<T>) new ProviderPriorityFooParamConverter("AAA");
   }
}
