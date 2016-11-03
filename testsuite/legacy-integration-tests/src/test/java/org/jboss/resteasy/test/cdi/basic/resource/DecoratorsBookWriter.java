package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(Constants.MEDIA_TYPE_TEST_XML)
public class DecoratorsBookWriter implements MessageBodyWriter<EJBBook> {
    private static MessageBodyWriter<EJBBook> delegate;

    static {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyWriter(EJBBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        boolean b = EJBBook.class.equals(type);
        return b;
    }

    public long getSize(EJBBook t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(EJBBook t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}

