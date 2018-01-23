package org.jboss.resteasy.test.providers.injection.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ApplicationInjectionApplicationParent extends Application
{
   @Override
   public Set<Class<?>> getClasses() {
      return new HashSet<>( Arrays.asList(
            ApplicationInjectionResource.class,
            ApplicationInjectionContainerRequestFilter.class,
            ApplicationInjectionContainerResponseFilter.class,
            ApplicationInjectionReaderInterceptor.class,
            ApplicationInjectionWriterInterceptor.class,
            ApplicationInjectionBodyReader.class,
            ApplicationInjectionBodyWriter.class,
            ApplicationInjectionParamConverterProvider.class,
            ApplicationInjectionExceptionMapper.class,
            ApplicationInjectionContextResolver.class,
            ApplicationInjectionDynamicFeature.class,
            ApplicationInjectionFeature.class,
            ApplicationInjectionAsyncResponseProvider.class
            ) );
   }
   
   public String getName() {
      return "ApplicationInjectionApplicationParent";
   }
}
