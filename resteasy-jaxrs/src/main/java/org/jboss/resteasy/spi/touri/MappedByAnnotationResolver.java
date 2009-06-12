package org.jboss.resteasy.spi.touri;

import java.lang.annotation.Annotation;

import org.jboss.resteasy.specimpl.UriBuilderImpl;

public class MappedByAnnotationResolver extends
      AbstractURITemplateAnnotationResolver
{
   protected Class<? extends Annotation> getAnnotationType()
   {
      return MappedBy.class;
   }

   protected UriBuilderImpl getUriBuilder(Class<? extends Object> clazz)
   {
      MappedBy mappedBy = clazz.getAnnotation(MappedBy.class);
      UriBuilderImpl uriBuilderImpl = new UriBuilderImpl();
      Class<?> resourceType = mappedBy.resource();
      uriBuilderImpl.path(resourceType);
      String method = mappedBy.method();
      if (method != null && method.length() > 0)
      {
         uriBuilderImpl.path(resourceType, method);
      }
      return uriBuilderImpl;
   }
}
