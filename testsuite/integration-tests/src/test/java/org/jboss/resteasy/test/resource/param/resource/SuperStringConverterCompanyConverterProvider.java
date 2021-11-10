package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterCompanyConverterProvider implements ParamConverterProvider {

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
   {
      if (!SuperStringConverterCompanyConverter.class.equals(rawType)) return null;
      return (ParamConverter<T>)new SuperStringConverterCompanyConverter();
   }

}
