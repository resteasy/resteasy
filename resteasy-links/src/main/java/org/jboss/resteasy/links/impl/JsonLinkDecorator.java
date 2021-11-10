package org.jboss.resteasy.links.impl;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinksProvider;
import org.jboss.resteasy.plugins.providers.jackson.DecoratedEntityContainer;
import org.jboss.resteasy.spi.DecoratorProcessor;

import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.util.Collection;

public class JsonLinkDecorator implements DecoratorProcessor<DecoratedEntityContainer, AddLinks> {

   @Override
   public DecoratedEntityContainer decorate(DecoratedEntityContainer target, AddLinks annotation, Class type, Annotation[] annotations, MediaType mediaType) {
      LinksInjector injector = new LinksInjector();
      LinksProvider<Object> provider = LinksProvider.getObjectLinksProvider();
      // find all rest service classes and scan them
      if (Collection.class.isAssignableFrom(target.getEntity().getClass())) {
         Collection coll = (Collection) target.getEntity();
         for (Object entity : coll) {
            injector.inject(entity, provider.getLinks(entity));
         }
      } else {
         injector.inject(target.getEntity(), provider.getLinks(target.getEntity()));
      }

      return target;
   }
}
