package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;

public class ClientErrorBadMediaTypeHeaderDelegate extends MediaTypeHeaderDelegate {
    public MediaType fromString(String type) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("MediaType value is null");
        }
        return parse(type);
    }

    public static MediaType parse(String type) {
        if ("text".equals(type)) {
            return new MediaType("text", "");
        }
        return MediaTypeHeaderDelegate.parse(type);
    }
}
