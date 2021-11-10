package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MultiValuedParamDefaultParamConverterParamConverterProvider implements ParamConverterProvider {

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {

      if (MultiValuedParamDefaultParamConverterParamConverterClass.class.equals(rawType)) {
         return (ParamConverter<T>) new MultiValuedParamDefaultParamConverterParamConverter();
      }
      return null;
   }
}
