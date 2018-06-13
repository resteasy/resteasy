package org.jboss.resteasy.test.providers.plain.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.DefaultNumberWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class DefaultNumberWriterCustom extends DefaultNumberWriter {
    public static volatile boolean used;
    private static Logger logger = Logger.getLogger(DefaultNumberWriterCustom.class);

    @Override
    public void writeTo(Number n, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        logger.info("DefaultNumberWriterCustom.writeTo()");
        used = true;
        super.writeTo(n, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}
