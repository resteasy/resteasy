package org.jboss.resteasy.links.impl;

import java.lang.annotation.Annotation;

import jakarta.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinksProvider;
import org.jboss.resteasy.spi.DecoratorProcessor;

public class LinkDecorator implements DecoratorProcessor<Marshaller, AddLinks> {

   public Marshaller decorate(Marshaller target, final AddLinks annotation,
                              Class type, Annotation[] annotations, MediaType mediaType) {

      target.setListener(new Listener() {
         @Override
         public void beforeMarshal(Object entity) {
            LinksInjector injector = new LinksInjector();
            injector.inject(entity, LinksProvider.getObjectLinksProvider().getLinks(entity));
         }
      });
      return target;
   }
}
