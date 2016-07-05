package org.jboss.resteasy.test.client.exception.resource;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;

import javax.ws.rs.core.MediaType;

public class ClientErrorBadMediaTypeHeaderDelegate extends MediaTypeHeaderDelegate {
    public Object fromString(String type) throws IllegalArgumentException {
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
