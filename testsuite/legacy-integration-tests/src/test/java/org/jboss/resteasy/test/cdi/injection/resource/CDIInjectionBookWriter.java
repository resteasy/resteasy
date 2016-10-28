package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
@Produces(Constants.MEDIA_TYPE_TEST_XML)
@ApplicationScoped
public class CDIInjectionBookWriter implements MessageBodyWriter<CDIInjectionBook> {
    private static MessageBodyWriter<CDIInjectionBook> delegate;

    @Inject
    private CDIInjectionDependentScoped dependent;
    @Inject
    private CDIInjectionStatefulEJB stateful;

    static {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyWriter(CDIInjectionBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CDIInjectionBook.class.equals(type);
    }

    public long getSize(CDIInjectionBook t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(CDIInjectionBook t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

    public CDIInjectionDependentScoped getDependent() {
        return dependent;
    }


    public CDIInjectionStatefulEJB getStateful() {
        return stateful;
    }
}

