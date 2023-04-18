package org.jboss.resteasy.core;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.LoggableFailure;

@SuppressWarnings("serial")
public class NoMessageBodyWriterFoundFailure extends LoggableFailure {

    public NoMessageBodyWriterFoundFailure(final @SuppressWarnings("rawtypes") Class type, final MediaType contentType) {
        super(
                String
                        .format(
                                "Could not find MessageBodyWriter for response object of type: %s of media type: %s",
                                type.getName(), contentType.toString()),
                HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
    }
}
