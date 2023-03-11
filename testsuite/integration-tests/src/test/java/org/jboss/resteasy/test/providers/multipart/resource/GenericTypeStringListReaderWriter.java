package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.test.providers.multipart.GenericTypeMultipartTest;

@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class GenericTypeStringListReaderWriter implements MessageBodyReader<List<String>>, MessageBodyWriter<List<String>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GenericTypeMultipartTest.stringListType.getType().equals(genericType);
    }

    @Override
    public long getSize(List<String> t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        List<String> list = t;
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            entityStream.write(it.next().getBytes());
            entityStream.write("\r".getBytes());
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GenericTypeMultipartTest.stringListType.getType().equals(genericType);
    }

    @Override
    public List<String> readFrom(Class<List<String>> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        List<String> list = new ArrayList<String>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte b = (byte) entityStream.read();
        while (b > -1) {
            while (b != '\r') {
                baos.write(b);
                b = (byte) entityStream.read();
            }
            list.add(new String(baos.toByteArray()));
            baos.reset();
            b = (byte) entityStream.read();
        }
        return list;
    }
}
