package org.jboss.resteasy.test.cdi.basic.resource;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class DecoratorsBookWriterInterceptor implements WriterInterceptor {
    @Inject
    private Logger log;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        log.info("entering DecoratorsBookWriterInterceptor.aroundWriteTo()");
        DecoratorsVisitList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_ENTER);
        context.proceed();
        DecoratorsVisitList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_LEAVE);
        log.info("leaving DecoratorsBookWriterInterceptor.aroundWriteTo()");
    }
}

