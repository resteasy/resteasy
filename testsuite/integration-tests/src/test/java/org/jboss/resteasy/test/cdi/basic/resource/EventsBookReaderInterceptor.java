package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class EventsBookReaderInterceptor implements ReaderInterceptor {
    @Inject
    @EventsReadIntercept
    Event<String> readInterceptEvent;

    @Inject
    private Logger log;

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        log.info("*** Intercepting call in EventsBookReaderInterceptor.read()");
        log.info("EventsBookReaderInterceptor firing readInterceptEvent");
        readInterceptEvent.fire("readInterceptEvent");
        Object result = context.proceed();
        log.info("*** Back from intercepting call in EventsBookReaderInterceptor.read()");
        return result;
    }

}
