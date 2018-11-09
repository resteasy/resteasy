package org.jboss.resteasy.links.impl;

import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.jboss.resteasy.spi.Registry;

public class LinkDecorator implements DecoratorProcessor<Marshaller, AddLinks> {

   public Marshaller decorate(Marshaller target, final AddLinks annotation,
         Class type, Annotation[] annotations, MediaType mediaType) {
      target.setListener(new Listener() {
         @Override
         public void beforeMarshal(Object entity) {
            UriInfo uriInfo = ResteasyContext.getContextData(UriInfo.class);
            ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyContext.getContextData(Registry.class);

            // find all rest service classes and scan them
            RESTUtils.addDiscovery(entity, uriInfo, registry);
         }
      });
      return target;
   }
}
