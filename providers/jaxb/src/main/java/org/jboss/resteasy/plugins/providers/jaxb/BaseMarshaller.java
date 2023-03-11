package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class BaseMarshaller implements Marshaller {
    protected Marshaller marshaller;
    protected String charset = StandardCharsets.UTF_8.name();

    public void marshal(Object o, OutputStream outputStream)
            throws JAXBException {
        try {
            marshal(o, new OutputStreamWriter(outputStream, charset));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setProperty(String s, Object o)
            throws PropertyException {
        marshaller.setProperty(s, o);
        if (s.equals(Marshaller.JAXB_ENCODING))
            charset = o.toString();
    }
}
