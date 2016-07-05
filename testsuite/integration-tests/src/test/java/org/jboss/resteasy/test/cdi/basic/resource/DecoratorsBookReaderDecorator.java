package org.jboss.resteasy.test.cdi.basic.resource;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

@Decorator
public abstract class DecoratorsBookReaderDecorator implements MessageBodyReader<EJBBook> {
    @Inject
    private Logger log;

    @Inject
    @Delegate
    private MessageBodyReader<EJBBook> reader;

    @Override
    public EJBBook readFrom(Class<EJBBook> type, Type genericType,
                         Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        log.info("entering DecoratorsBookReaderDecorator.readFrom()");
        DecoratorsVisitList.add(DecoratorsVisitList.READER_DECORATOR_ENTER);
        EJBBook b = reader.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
        DecoratorsVisitList.add(DecoratorsVisitList.READER_DECORATOR_LEAVE);
        log.info("leaving DecoratorsBookReaderDecorator.readFrom()");
        return b;
    }
}
