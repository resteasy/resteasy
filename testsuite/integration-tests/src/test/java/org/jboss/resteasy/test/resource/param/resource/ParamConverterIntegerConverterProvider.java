package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ParamConverterIntegerConverterProvider implements ParamConverterProvider {
   @SuppressWarnings(value = "unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      if (!int.class.equals(rawType)) {
         return null;
      }
      return (ParamConverter<T>) new ParamConverterIntegerConverter();
   }
}