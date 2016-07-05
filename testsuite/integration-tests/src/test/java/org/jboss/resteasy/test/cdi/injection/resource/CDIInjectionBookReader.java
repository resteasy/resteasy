package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class CDIInjectionBookReader implements MessageBodyReader<CDIInjectionBook> {
    private static MessageBodyReader<CDIInjectionBook> delegate;

    private static Logger log = Logger.getLogger(CDIInjectionBookReader.class);

    @Inject
    private CDIInjectionDependentScoped dependent;
    @Inject
    private CDIInjectionStatefulEJB stateful;

    static {
        log.info("In BookReader static {}");
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyReader(CDIInjectionBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
        log.info("In BookReader static {}");
    }

    public CDIInjectionBookReader() {
        log.info("entered BookReader()");
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CDIInjectionBook.class.equals(type);
    }

    public CDIInjectionBook readFrom(Class<CDIInjectionBook> type, Type genericType,
                         Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        log.info("entering BookReader.readFrom()");
        return CDIInjectionBook.class.cast(delegate.readFrom(CDIInjectionBook.class, genericType, annotations, mediaType, httpHeaders, entityStream));
    }

    public CDIInjectionDependentScoped getDependent() {
        return dependent;
    }

    public CDIInjectionStatefulEJB getStateful() {
        return stateful;
    }
}

