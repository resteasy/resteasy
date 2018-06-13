package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.util.TypeConverter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
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

@Provider
@Produces("*/*")
@Consumes("*/*")
public class WriterNotBuiltinTestWriter implements MessageBodyWriter, MessageBodyReader {
    private static Logger logger = Logger.getLogger(WriterNotBuiltinTestWriter.class);

    public static volatile boolean used;

    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return o.toString().getBytes().length;
    }

    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(o.toString().getBytes());
        logger.info("my writeTo");
        used = true;
    }

    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @SuppressWarnings(value = "unchecked")
    public Object readFrom(Class type, Type genericType,
                           Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String value = ProviderHelper.readString(entityStream, mediaType);
        return TypeConverter.getType(type, value);
    }
}
