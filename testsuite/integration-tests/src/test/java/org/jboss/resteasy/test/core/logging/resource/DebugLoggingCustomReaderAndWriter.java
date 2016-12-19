package org.jboss.resteasy.test.core.logging.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.util.NoContent;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
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
import java.nio.charset.StandardCharsets;

@Provider
@Produces("aaa/bbb")
@Consumes("aaa/bbb")
public class DebugLoggingCustomReaderAndWriter implements MessageBodyReader<String>, MessageBodyWriter<String> {
    private static Logger logger = Logger.getLogger(DebugLoggingCustomReaderAndWriter.class);

    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    public long getSize(String o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return o.getBytes().length;
    }

    public void writeTo(String o,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException {
        logger.info("my writeTo");
        String charset = mediaType.getParameters().get("charset");
        if (charset == null) entityStream.write(o.getBytes(StandardCharsets.UTF_8));
        else entityStream.write(o.getBytes(charset));

    }

    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }


    @SuppressWarnings(value = "unchecked")
    public String readFrom(Class<String> type,
                           Type genericType,
                           Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException {
        logger.info("my readFrom");
        if (NoContent.isContentLengthZero(httpHeaders)) return "";
        return ProviderHelper.readString(entityStream, mediaType);
    }
}
