package org.resteasy;

import org.resteasy.spi.HttpInputMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ProviderFactory;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterExtractor implements ParameterExtractor {
    private Class type;
    private ProviderFactory factory;

    public MessageBodyParameterExtractor(Class type, ProviderFactory factory) {
        this.type = type;
        this.factory = factory;
    }

    public Object extract(HttpInputMessage request) {
        try {
            MediaType mediaType = request.getHttpHeaders().getMediaType();
            MessageBodyReader reader = factory.createMessageBodyReader(type, mediaType);
            return reader.readFrom(type, mediaType, request.getHttpHeaders().getRequestHeaders(), request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failure extracting body", e);
        }
    }
}
