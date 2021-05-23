package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.logging.Logger;

@Decorator
public abstract class DecoratorsRequestFilterDecorator implements ContainerRequestFilter {
   @Inject
   private Logger log;

   @Inject
   @Delegate
   private DecoratorsTestRequestFilter filter;

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {
      log.info("entering DecoratorsRequestFilterDecorator.filter()");
      DecoratorsVisitList.add(DecoratorsVisitList.REQUEST_FILTER_DECORATOR_ENTER);
      filter.filter(requestContext);
      DecoratorsVisitList.add(DecoratorsVisitList.REQUEST_FILTER_DECORATOR_LEAVE);
      log.info("leaving DecoratorsRequestFilterDecorator.filter()");
   }
}
