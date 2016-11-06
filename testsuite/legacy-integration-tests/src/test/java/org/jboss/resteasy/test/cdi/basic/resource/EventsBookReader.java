package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class EventsBookReader implements MessageBodyReader<EJBBook> {
    private static Logger log = Logger.getLogger(EventsBookReader.class);

    private static MessageBodyReader<EJBBook> delegate;

    @Inject
    @EventsRead(context = "reader")
    Event<String> readEvent;

    private ArrayList<Object> eventList = new ArrayList<Object>();

    static {
        log.info("In BookReader static {}");
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyReader(EJBBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
        log.info("In BookReader static {}");
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        log.info("entering BookReader.isReadable()");
        boolean b = EJBBook.class.equals(type);
        log.info("leaving BookReader.isReadable()");
        return b;
    }

    public EJBBook readFrom(Class<EJBBook> type, Type genericType,
                            Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        log.info("entering BookReader.readFrom()");
        EJBBook book = EJBBook.class.cast(delegate.readFrom(EJBBook.class, genericType, annotations, mediaType, httpHeaders, entityStream));
        log.info("BookReader firing readEvent");
        readEvent.fire("readEvent");
        log.info("BookReader.readFrom() read " + book);
        log.info("leaving BookReader.readFrom()");
        return book;
    }

    public void readIntercept(@Observes @EventsReadIntercept String event) {
        eventList.add(event);
        log.info("BookReader.readIntercept() got " + event);
    }

    public void read(@Observes @EventsRead(context = "reader") String event) {
        eventList.add(event);
        log.info("BookReader.read() got " + event);
    }

    public void writeIntercept(@Observes @EventsWriteIntercept String event) {
        eventList.add(event);
        log.info("BookReader.writeIntercept() got " + event);
    }

    public void write(@Observes @EventsWrite(context = "writer") String event) {
        eventList.add(event);
        log.info("BookReader.write() got " + event);
    }

    public void process(@Observes @EventsProcess String event) {
        eventList.add(event);
        log.info("BookReader.process() got " + event);
    }

    public void processRead(@Observes @EventsProcess @EventsRead(context = "resource") String event) {
        eventList.add(event);
        log.info("BookReader.processRead() got " + event);
    }

    public void processWrite(@Observes @EventsProcess @EventsWrite(context = "resource") String event) {
        eventList.add(event);
        log.info("BookReader.processWrite() got " + event);
    }

    public void unused(@Observes @EventsRead(context = "unused") @EventsWrite(context = "unused") EventsUnused event) {
        eventList.add(event);
        log.info("BookReader.unused() got " + event);
        throw new RuntimeException("BookReader.unused() got " + event);
    }

    public ArrayList<Object> getEventList() {
        return new ArrayList<Object>(eventList);
    }
}

