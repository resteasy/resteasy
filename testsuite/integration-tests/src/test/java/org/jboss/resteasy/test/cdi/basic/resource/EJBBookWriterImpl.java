package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.persistence.Entity;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

@Stateful
@LocalBean
@Provider
@Produces(Constants.MEDIA_TYPE_TEST_XML)
public class EJBBookWriterImpl implements MessageBodyWriter<EJBBook> {

    private static Logger logger = Logger.getLogger(EJBBookWriterImpl.class);

    @SuppressWarnings("rawtypes")
    private static MessageBodyWriter delegate;
    private static int uses;

    @Entity
    @XmlRootElement(name = "nonbook")
    @XmlAccessorType(XmlAccessType.FIELD)
    public class NonBook {
    }

    /*
     * It seems that EJBBookWriterImpl is treated somewhat differently than EJBBookReaderImpl, perhaps
     * because EJBBookWriterImpl has a no-interface view. In any case, EJBBookReaderImpl is able to
     * get an instance of ResteasyProviderFactory in a static block, but EJBBookWriterImpl isn't.
     */
    static void getDelegate() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyWriter(NonBook.class, null, null, MediaType.APPLICATION_XML_TYPE);
        logger.info("writer delegate: " + delegate); // Should be JAXBXmlRootElementProvider.
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return EJBBook.class.equals(type);
    }

    public long getSize(EJBBook t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeTo(EJBBook t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        if (delegate == null) {
            getDelegate();
        }
        delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        uses++;
    }

    public int getUses() {
        return uses;
    }

    public void reset() {
        uses = 0;
    }
}
