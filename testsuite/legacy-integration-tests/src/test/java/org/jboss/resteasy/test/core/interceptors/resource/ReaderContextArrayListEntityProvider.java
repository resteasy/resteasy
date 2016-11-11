package org.jboss.resteasy.test.core.interceptors.resource;

import org.jboss.resteasy.utils.TestUtil;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Provider
public class ReaderContextArrayListEntityProvider implements
        MessageBodyReader<ArrayList<String>>,
        MessageBodyWriter<ArrayList<String>> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return type == ArrayList.class;
    }

    @Override
    public long getSize(ArrayList<String> t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        int annlen = annotations.length > 0 ? annotations[0].annotationType()
                .getName().length() : 0;
        return t.iterator().next().length() + annlen
                + mediaType.toString().length();
    }

    @Override
    public void writeTo(ArrayList<String> t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException,
            WebApplicationException {
        String ann = "";
        if (annotations.length > 0) {
            ann = annotations[0].annotationType().getName();
        }
        entityStream.write((t.iterator().next() + ann + mediaType.toString())
                .getBytes());
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return type == ArrayList.class;
    }

    @Override
    public ArrayList<String> readFrom(Class<ArrayList<String>> type,
                                      Type genericType, Annotation[] annotations, MediaType mediaType,
                                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String text = TestUtil.readString(entityStream);
        entityStream.close();
        String ann = "";
        if (annotations.length > 0) {
            ann = annotations[0].annotationType().getName();
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(text + ann + mediaType.toString());
        return list;
    }
}
