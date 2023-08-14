package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Provider
public class ProviderOneArgConstructorStringHandlerBodyWriter
        implements MessageBodyWriter<ProviderOneArgConstructorStringHandler> {

    @Inject
    @SuppressWarnings("unused")
    public ProviderOneArgConstructorStringHandlerBodyWriter(final Application application) {
    }

    @FollowUpRequired("This should not be required, but currently RESTEasy requires it")
    @SuppressWarnings("unused")
    public ProviderOneArgConstructorStringHandlerBodyWriter() {
    }

    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations,
            MediaType mediaType) {
        return ProviderOneArgConstructorStringHandler.class.equals(type);
    }

    @Override
    public void writeTo(ProviderOneArgConstructorStringHandler t, Class<?> type,
            Type genericType, Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
        entityStream.write(t.getC().getBytes());
    }
}
