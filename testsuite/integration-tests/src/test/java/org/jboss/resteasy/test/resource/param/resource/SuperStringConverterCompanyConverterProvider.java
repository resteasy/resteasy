package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

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
