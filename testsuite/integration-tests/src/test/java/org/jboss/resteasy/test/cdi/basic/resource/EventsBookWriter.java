package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

@Provider
@Produces(Constants.MEDIA_TYPE_TEST_XML)
public class EventsBookWriter implements MessageBodyWriter<EJBBook> {
    private static MessageBodyWriter<EJBBook> delegate;

    @Inject
    @EventsWrite(context = "writer")
    Event<String> writeEvent;

    @Inject
    private Logger log;

    static {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyWriter(EJBBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        log.info("entering BookWriter.isWriteable()");
        boolean b = EJBBook.class.equals(type);
        log.info("leaving BookWriter.isWriteable()");
        return b;
    }

    public long getSize(EJBBook t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        log.info("entering BookWriter.getSize()");
        log.info("leaving BookWriter.getSize()");
        return -1;
    }

    @Override
    public void writeTo(EJBBook t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        log.info("entering BookWriter.writeTo()");
        log.info("BookWriter.writeTo() writing " + t);
        delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        log.info("BookWriter firing writeEvent");
        writeEvent.fire("writeEvent");
        log.info("leaving BookWriter.writeTo()");
    }
}
