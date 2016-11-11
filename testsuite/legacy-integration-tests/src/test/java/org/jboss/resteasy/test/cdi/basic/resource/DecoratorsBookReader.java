package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

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
import java.util.logging.Logger;

@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class DecoratorsBookReader implements MessageBodyReader<EJBBook> {
    private static MessageBodyReader<EJBBook> delegate;

    @Inject
    private Logger log;

    static {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyReader(EJBBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        log.info("entering DecoratorsBookReader.isReadable()");
        boolean b = EJBBook.class.equals(type);
        log.info("leaving DecoratorsBookReader.isReadable()");
        return b;
    }

    public EJBBook readFrom(Class<EJBBook> type, Type genericType,
                         Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        log.info("entering DecoratorsBookReader.readFrom()");
        EJBBook book = EJBBook.class.cast(delegate.readFrom(EJBBook.class, genericType, annotations, mediaType, httpHeaders, entityStream));
        log.info("DecoratorsBookReader.readFrom() read " + book);
        log.info("leaving DecoratorsBookReader.readFrom()");
        return book;
    }
}

