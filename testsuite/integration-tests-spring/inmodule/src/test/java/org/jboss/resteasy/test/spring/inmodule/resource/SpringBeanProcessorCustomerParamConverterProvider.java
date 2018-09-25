package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class SpringBeanProcessorCustomerParamConverterProvider implements ParamConverterProvider {

   // this isn't a complex service, but it provides a test to confirm that
   // RESTEasy doesn't muck up the @Autowired annotation handling in the Spring
   // life-cycle
   @Autowired
   SpringBeanProcessorCustomerService service;

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
   {
      if (!SpringBeanProcessorCustomerParamConverter.class.equals(rawType)) return null;
      return (ParamConverter<T>)new SpringBeanProcessorCustomerParamConverter();
   }

}
