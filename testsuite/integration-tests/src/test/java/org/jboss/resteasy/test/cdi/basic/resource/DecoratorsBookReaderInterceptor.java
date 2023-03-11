package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class DecoratorsBookReaderInterceptor implements ReaderInterceptor {
    @Inject
    private Logger log;

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        log.info("entering DecoratorsBookReaderInterceptor.aroundReadFrom()");
        DecoratorsVisitList.add(DecoratorsVisitList.READER_INTERCEPTOR_ENTER);
        Object result = context.proceed();
        DecoratorsVisitList.add(DecoratorsVisitList.READER_INTERCEPTOR_LEAVE);
        log.info("leaving DecoratorsBookReaderInterceptor.aroundReadFrom()");
        return result;
    }
}
