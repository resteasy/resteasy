package org.jboss.resteasy.test.cdi.basic.resource;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.logging.Logger;

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

