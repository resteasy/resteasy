package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.persistence.Entity;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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
     * because EJBBookWriterImpl has a no-interface view.  In any case, EJBBookReaderImpl is able to
     * get an instance of ResteasyProviderFactory in a static block, but EJBBookWriterImpl isn't.
     */
    static void getDelegate() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyWriter(NonBook.class, null, null, MediaType.APPLICATION_XML_TYPE);
        logger.info("writer delegate: " + delegate);  // Should be JAXBXmlRootElementProvider.
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

