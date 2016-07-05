package org.jboss.resteasy.test.providers.custom.resource;

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

@Provider
@Produces("application/xml")
public class ReaderWriterCustomerWriter implements MessageBodyWriter<ReaderWriterCustomer> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(ReaderWriterCustomer.class);
    }

    public long getSize(ReaderWriterCustomer customer, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    public void writeTo(ReaderWriterCustomer customer, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        String out = "<customer><name>" + customer.getName() + "</name></customer>";
        entityStream.write(out.getBytes());
    }
}
