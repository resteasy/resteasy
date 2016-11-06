package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.resteasy.test.providers.custom.CollectionProviderTest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

@Provider
public class CollectionProviderIncorrectCollectionWriter
        implements MessageBodyWriter<Collection<?>> {

    public static final String ERROR = "ERROR ";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return !new CollectionProviderCollectionWriter().isWriteable(type, genericType,
                annotations, mediaType);
    }

    @Override
    public long getSize(Collection<?> t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        String path = CollectionProviderTest.getPathValue(annotations);
        return ERROR.length() + path.length();
    }

    @Override
    public void writeTo(Collection<?> t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException,
            WebApplicationException {
        String path = CollectionProviderTest.getPathValue(annotations);
        entityStream.write(ERROR.getBytes());
        entityStream.write(path.getBytes());
    }
}
