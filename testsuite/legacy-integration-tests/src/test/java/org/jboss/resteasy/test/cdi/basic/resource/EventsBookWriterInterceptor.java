package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
@ServerInterceptor
public class EventsBookWriterInterceptor implements WriterInterceptor {
    @Inject
    @EventsWriteIntercept
    Event<String> writeInterceptEvent;

    @Inject
    private Logger log;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        log.info("*** Intercepting call in EventsBookWriterInterceptor.write()");
        log.info("EventsBookWriterInterceptor firing writeInterceptEvent");
        writeInterceptEvent.fire("writeInterceptEvent");
        context.proceed();
        log.info("*** Back from intercepting call in EventsBookWriterInterceptor.write()");
    }

}

