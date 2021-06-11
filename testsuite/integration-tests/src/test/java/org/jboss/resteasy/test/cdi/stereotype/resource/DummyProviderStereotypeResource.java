package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ProviderStereotype;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@ProviderStereotype
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class DummyProviderStereotypeResource implements MessageBodyWriter<Dummy> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Dummy dummy, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return dummy.getAge();
    }

    @Override
    public void writeTo(Dummy dummy, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write("Dummy provider".getBytes());
    }
}
