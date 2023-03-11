package org.jboss.resteasy.test.providers.jaxb.resource;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.jboss.resteasy.core.ExceptionAdapter;

public final class XmlJaxbProvidersHelper {

    /**
     * An HTTP Header than can be passed in order to have the XML response formatted.
     */
    public static final String FORMAT_XML_HEADER = "X-Xml-Formatted";

    private XmlJaxbProvidersHelper() {
    }

    public static <T> JAXBElement<T> unmarshall(Class<T> type, InputStream entityStream) {
        XMLStreamReader reader = XmlStreamFactory.getXMLStreamReader(entityStream);
        return unmarshall(type, entityStream, reader);
    }

    public static <T> JAXBElement<T> unmarshall(Class<T> type,
            InputStream entityStream,
            XMLStreamReader reader) {
        JAXBContext jaxb = JAXBCache.instance().getJAXBContext(type);
        return unmarshall(jaxb, type, entityStream, reader);
    }

    public static <T> JAXBElement<T> unmarshall(JAXBContext jaxb,
            Class<T> type,
            InputStream entityStream,
            XMLStreamReader reader) {
        try {
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            JAXBElement<T> e = unmarshaller.unmarshal(reader, type);
            return e;
        } catch (JAXBException e) {
            throw new ExceptionAdapter(e);
        }
    }

    public static String getCharset(final MediaType mediaType) {
        if (mediaType != null) {
            return mediaType.getParameters().get("charset");
        }
        return null;
    }

}
