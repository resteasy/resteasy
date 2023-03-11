package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ejb.Stateful;
import jakarta.persistence.Entity;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

@Stateful
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class EJBBookReaderImpl implements EJBBookReader, MessageBodyReader<EJBBook> {

    private static Logger log = Logger.getLogger(EJBBookReaderImpl.class);

    @SuppressWarnings("rawtypes")
    private static MessageBodyReader delegate;

    private static int uses;

    @Entity
    @XmlRootElement(name = "nonbook")
    @XmlAccessorType(XmlAccessType.FIELD)
    public class NonBook {
    }

    static {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyReader(NonBook.class, null, null, MediaType.APPLICATION_XML_TYPE);
        log.info("reader delegate: " + delegate); // Should be JAXBXmlRootElementProvider.
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return EJBBook.class.equals(type);
    }

    @SuppressWarnings("unchecked")
    public EJBBook readFrom(Class<EJBBook> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        log.info("entering EJBBookReader.readFrom()");
        uses++;
        return EJBBook.class
                .cast(delegate.readFrom(EJBBook.class, genericType, annotations, mediaType, httpHeaders, entityStream));
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public void reset() {
        uses = 0;
    }
}
