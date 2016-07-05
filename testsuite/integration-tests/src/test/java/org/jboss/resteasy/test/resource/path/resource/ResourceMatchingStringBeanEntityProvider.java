package org.jboss.resteasy.test.resource.path.resource;

import org.jboss.resteasy.test.resource.path.ResourceMatchingTest;

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
