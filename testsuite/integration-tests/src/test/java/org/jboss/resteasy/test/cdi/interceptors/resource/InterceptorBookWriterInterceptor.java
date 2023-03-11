package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

@Provider
public class InterceptorBookWriterInterceptor implements WriterInterceptor {
    @Inject
    private Logger log;

    @Override
    @InterceptorWriterBinding
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        log.info("*** Intercepting call in InterceptorBookWriterInterceptor.aroundWriteTo()");
        InterceptorVisitList.add(this);
        context.proceed();
        log.info("*** Back from intercepting call in InterceptorBookWriterInterceptor.aroundWriteTo()");
    }

}
