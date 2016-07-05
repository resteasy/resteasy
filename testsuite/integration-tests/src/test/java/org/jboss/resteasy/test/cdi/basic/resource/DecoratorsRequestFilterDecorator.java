package org.jboss.resteasy.test.cdi.basic.resource;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
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
