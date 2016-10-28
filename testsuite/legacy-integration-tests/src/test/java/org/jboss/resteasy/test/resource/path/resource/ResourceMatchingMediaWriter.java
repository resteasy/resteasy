package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

@Provider
@Produces(MediaType.APPLICATION_SVG_XML)
public class ResourceMatchingMediaWriter implements MessageBodyWriter<List<?>> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(List<?> t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return List.class.getSimpleName().length();
    }

    @Override
    public void writeTo(List<?> t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException,
            WebApplicationException {
        entityStream.write(List.class.getSimpleName().getBytes());
    }

}
