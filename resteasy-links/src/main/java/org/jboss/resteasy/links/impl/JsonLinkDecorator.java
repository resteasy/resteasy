package org.jboss.resteasy.links.impl;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.plugins.providers.jackson.DecoratedEntityContainer;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.jboss.resteasy.spi.Registry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.lang.annotation.Annotation;
import java.util.Collection;

public class JsonLinkDecorator implements DecoratorProcessor<DecoratedEntityContainer, AddLinks> {

   @Override
   public DecoratedEntityContainer decorate(DecoratedEntityContainer target, AddLinks annotation, Class type, Annotation[] annotations, MediaType mediaType) {
      UriInfo uriInfo = ResteasyContext.getContextData(UriInfo.class);
      ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyContext.getContextData(Registry.class);

      // find all rest service classes and scan them
      if (Collection.class.isAssignableFrom(target.getEntity().getClass())) {
         Collection coll = (Collection) target.getEntity();
         for (Object entity : coll) {
            RESTUtils.addDiscovery(entity, uriInfo, registry);
         }
      } else {
         RESTUtils.addDiscovery(target.getEntity(), uriInfo, registry);
      }

      return target;
   }
}
