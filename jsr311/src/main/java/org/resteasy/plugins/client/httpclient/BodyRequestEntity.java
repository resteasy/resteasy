package org.resteasy.plugins.client.httpclient;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.resteasy.MessageBodyParameterMarshaller;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BodyRequestEntity implements RequestEntity {

    private MessageBodyParameterMarshaller marshaller;
    private Object object;
    private MultivaluedMap<String, String> httpHeaders;

    public BodyRequestEntity(Object object, MessageBodyParameterMarshaller marshaller, MultivaluedMap<String, String> httpHeaders) {
        this.marshaller = marshaller;
        this.object = object;
        this.httpHeaders = httpHeaders;
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(OutputStream outputStream) throws IOException {
        marshaller.getMessageBodyWriter().writeTo(object, marshaller.getMediaType(), httpHeaders, outputStream);
    }

    public long getContentLength() {
        return marshaller.getMessageBodyWriter().getSize(object);
    }

    public String getContentType() {
        return marshaller.getMediaType().toString();
    }
}
