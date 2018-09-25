package org.jboss.resteasy.plugins.touri;

import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyUriBuilder;
import org.jboss.resteasy.spi.touri.URITemplate;

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
      ResteasyUriBuilder uriBuilderImpl = new ResteasyUriBuilderImpl();
      uriBuilderImpl.replacePath(uriTemplate);
      return uriBuilderImpl;
   }
}
