package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

@Decorator
public abstract class DecoratorsResponseFilterDecorator implements ContainerResponseFilter {
    @Inject
    private Logger log;

    @Inject
    @Delegate
    private DecoratorsResponseFilter filter;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.info("entering DecoratorsRequestFilterDecorator.filter()");
        DecoratorsVisitList.add(DecoratorsVisitList.RESPONSE_FILTER_DECORATOR_ENTER);
        filter.filter(requestContext, responseContext);
        DecoratorsVisitList.add(DecoratorsVisitList.RESPONSE_FILTER_DECORATOR_LEAVE);
        log.info("leaving DecoratorsRequestFilterDecorator.filter()");
    }
}
