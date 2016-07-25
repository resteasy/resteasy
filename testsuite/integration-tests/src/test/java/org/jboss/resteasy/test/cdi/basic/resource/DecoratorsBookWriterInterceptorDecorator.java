package org.jboss.resteasy.test.cdi.basic.resource;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.util.logging.Logger;

@Decorator
public abstract class DecoratorsBookWriterInterceptorDecorator implements WriterInterceptor {
    @Inject
    private Logger log;

    @Inject
    @Delegate
    private DecoratorsBookWriterInterceptor interceptor;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws java.io.IOException, javax.ws.rs.WebApplicationException {
        log.info("entering DecoratorsBookWriterInterceptorDecorator.aroundWriteTo()");
        DecoratorsVisitList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_DECORATOR_ENTER);
        interceptor.aroundWriteTo(context);
        DecoratorsVisitList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_DECORATOR_LEAVE);
        log.info("leaving DecoratorsBookWriterInterceptorDecorator.aroundWriteTo()");
    }
}
