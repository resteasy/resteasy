package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * An extension of ClientRequestHeaders that helps decorate the headers with a TrackingMap.
 */
public class TrackingClientRequestHeaders extends ClientRequestHeaders {

    public TrackingClientRequestHeaders(final ClientConfiguration configuration, final CaseInsensitiveMap<Object> headers) {
        super(configuration);
        this.headers = new TrackingMap<>(headers);
    }

    @Override
    public TrackingMap<Object> getHeaders() {
        return (TrackingMap<Object>) super.getHeaders();
    }
}
