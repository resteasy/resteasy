package org.resteasy;

import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.spi.HttpOutputMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ProviderFactory;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterMarshaller implements ParameterMarshaller {
    private Class type;
    private ProviderFactory factory;
    private MediaType mediaType;

    public MessageBodyParameterMarshaller(MediaType mediaType, Class type, ProviderFactory factory) {
        this.type = type;
        this.factory = factory;
        this.mediaType = mediaType;
    }

    public void marshall(Object obj, UriBuilderImpl uri, HttpOutputMessage output) {
        try {
            MessageBodyWriter writer = getMessageBodyWriter();
            writer.writeTo(obj, mediaType, output.getOutputHeaders(), output.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failure marshalling body", e);
        }
    }

    public MessageBodyWriter getMessageBodyWriter() {
        MessageBodyWriter writer = factory.createMessageBodyWriter(type, mediaType);
        return writer;
    }

    public Class getType() {
        return type;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}