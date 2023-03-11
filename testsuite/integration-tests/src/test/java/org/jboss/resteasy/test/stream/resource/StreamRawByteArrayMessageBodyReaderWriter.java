package org.jboss.resteasy.test.stream.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Consumes("*/*")
@Produces("*/*")
@Provider
public class StreamRawByteArrayMessageBodyReaderWriter implements MessageBodyReader<Byte[]>, MessageBodyWriter<Byte[]> {

    private static final Logger LOG = Logger.getLogger(StreamRawByteArrayMessageBodyReaderWriter.class);

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        LOG.info(this + ": type: " + type + ": " + (byte.class.equals(type) || Byte.class.equals(type)));
        return Byte[].class.equals(type);
    }

    @Override
    public void writeTo(Byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        byte[] bs = new byte[t.length];
        for (int i = 0; i < t.length; i++) {
            bs[i] = (byte) t[i];
        }
        entityStream.write(bs);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Byte[].class.equals(type);
    }

    @Override
    public Byte[] readFrom(Class<Byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        ArrayList<Byte> list = new ArrayList<Byte>();
        int b = entityStream.read();
        while (b != -1) {
            list.add((byte) b);
            b = entityStream.read();
        }
        return list.toArray(new Byte[list.size()]);
    }
}
