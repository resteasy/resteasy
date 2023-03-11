package org.jboss.resteasy.test.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.test.client.ClientProviderTest;

public class ClientProviderStringEntityProviderReader implements MessageBodyReader<String> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type == String.class;
    }

    @Override
    public String readFrom(Class<String> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String text = ClientProviderTest.readFromStream(entityStream);
        entityStream.close();
        String result = "Application defined provider reader: " + text;
        return result;
    }
}
