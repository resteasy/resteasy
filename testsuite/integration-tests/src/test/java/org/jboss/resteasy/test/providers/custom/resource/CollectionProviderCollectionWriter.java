package org.jboss.resteasy.test.providers.custom.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.test.providers.custom.CollectionProviderTest;

@Provider
public class CollectionProviderCollectionWriter implements
        MessageBodyWriter<Collection<?>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        String path = CollectionProviderTest.getPathValue(annotations);
        // Return type : Other
        if (path.equalsIgnoreCase(type.getSimpleName())) {
            return CollectionProviderTest.checkOther(type, genericType);
        } else if (path.equalsIgnoreCase("response/linkedlist")) {
            return CollectionProviderTest.checkResponseNongeneric(type, genericType);
        } else if (path.equalsIgnoreCase("response/genericentity/linkedlist")) {
            return CollectionProviderTest.checkGeneric(type, genericType);
        } else if (path.equalsIgnoreCase("genericentity/linkedlist")) {
            return CollectionProviderTest.checkGeneric(type, genericType);
        }
        return false;
    }

    @Override
    public long getSize(Collection<?> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return Response.Status.OK.name().length();
    }

    @Override
    public void writeTo(Collection<?> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        entityStream.write(Response.Status.OK.name().getBytes());
    }

}
