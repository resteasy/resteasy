package org.jboss.resteasy.test.cdi.basic.resource;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
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
