package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

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
