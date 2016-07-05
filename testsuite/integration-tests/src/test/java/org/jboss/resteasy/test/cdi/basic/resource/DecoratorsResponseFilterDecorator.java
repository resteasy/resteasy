package org.jboss.resteasy.test.cdi.basic.resource;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.util.logging.Logger;

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
