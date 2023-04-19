package org.jboss.resteasy.test.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

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
