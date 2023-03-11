package org.jboss.resteasy.test.response.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.plugins.providers.ProviderHelper;

@Produces("text/plain")
@Consumes("text/plain")
public class ProduceConsumeTextData implements MessageBodyReader<ProduceConsumeData>, MessageBodyWriter<ProduceConsumeData> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(ProduceConsumeData.class);
    }

    @Override
    public ProduceConsumeData readFrom(Class<ProduceConsumeData> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String str = ProviderHelper.readString(entityStream, mediaType);
        return new ProduceConsumeData(str, "text");
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(ProduceConsumeData.class);
    }

    @Override
    public long getSize(ProduceConsumeData data, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ProduceConsumeData data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        String str = data.data + ":" + data.type + ":text";
        entityStream.write(str.getBytes());
    }
}
