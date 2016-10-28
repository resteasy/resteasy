package org.jboss.resteasy.test.cdi.basic.resource;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.logging.Logger;

@Decorator
public abstract class DecoratorsBookReaderInterceptorDecorator implements ReaderInterceptor {
    @Inject
    private Logger log;

    @Inject
    @Delegate
    private DecoratorsBookReaderInterceptor interceptor;

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        log.info("entering DecoratorsBookReaderInterceptorDecorator.aroundReadFrom()");
        DecoratorsVisitList.add(DecoratorsVisitList.READER_INTERCEPTOR_DECORATOR_ENTER);
        Object o = interceptor.aroundReadFrom(context);
        DecoratorsVisitList.add(DecoratorsVisitList.READER_INTERCEPTOR_DECORATOR_LEAVE);
        log.info("leaving DecoratorsBookReaderInterceptorDecorator.aroundReadFrom()");
        return o;
    }
}
