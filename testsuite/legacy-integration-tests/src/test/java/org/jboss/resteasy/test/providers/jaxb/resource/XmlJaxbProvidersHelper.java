package org.jboss.resteasy.test.providers.jaxb.resource;


import org.jboss.resteasy.core.ExceptionAdapter;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

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
