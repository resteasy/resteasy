package org.jboss.resteasy.test.resource.path.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.test.resource.path.ResourceMatchingTest;

@Provider
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class ResourceMatchingStringBeanEntityProvider implements MessageBodyReader<ResourceMatchingStringBean>,
        MessageBodyWriter<ResourceMatchingStringBean> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return ResourceMatchingStringBean.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(ResourceMatchingStringBean t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return t.get().length();
    }

    @Override
    public void writeTo(ResourceMatchingStringBean t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        entityStream.write(t.get().getBytes());
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return isWriteable(type, genericType, annotations, mediaType);
    }

    @Override
    public ResourceMatchingStringBean readFrom(Class<ResourceMatchingStringBean> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String stream = ResourceMatchingTest.readFromStream(entityStream);
        ResourceMatchingStringBean bean = new ResourceMatchingStringBean(stream);
        return bean;
    }

}
