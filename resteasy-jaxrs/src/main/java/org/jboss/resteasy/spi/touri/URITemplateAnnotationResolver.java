package org.jboss.resteasy.spi.touri;

import org.jboss.resteasy.specimpl.ResteasyUriBuilder;

import java.lang.annotation.Annotation;

public class URITemplateAnnotationResolver extends
        AbstractURITemplateAnnotationResolver
{

   protected Class<? extends Annotation> getAnnotationType()
   {
      return URITemplate.class;
   }

   protected ResteasyUriBuilder getUriBuilder(Class<? extends Object> clazz)
   {
      String uriTemplate = clazz.getAnnotation(URITemplate.class).value();
      ResteasyUriBuilder uriBuilderImpl = new ResteasyUriBuilder();
      uriBuilderImpl.replacePath(uriTemplate);
      return uriBuilderImpl;
   }
}
