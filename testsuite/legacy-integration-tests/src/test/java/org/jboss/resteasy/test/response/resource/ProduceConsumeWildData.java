package org.jboss.resteasy.test.response.resource;

import org.jboss.resteasy.plugins.providers.ProviderHelper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ProduceConsumeWildData implements MessageBodyReader<ProduceConsumeData>, MessageBodyWriter<ProduceConsumeData> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(ProduceConsumeData.class);
    }

    @Override
    public ProduceConsumeData readFrom(Class<ProduceConsumeData> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        String str = ProviderHelper.readString(entityStream, mediaType);
        return new ProduceConsumeData(str, "wild");
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(ProduceConsumeData.class);
    }

    @Override
    public long getSize(ProduceConsumeData data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ProduceConsumeData data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        String str = data.data + ":" + data.type + ":wild";
        entityStream.write(str.getBytes());
    }
}
