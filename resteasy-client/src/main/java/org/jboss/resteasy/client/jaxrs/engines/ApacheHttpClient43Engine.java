package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

/**
 * An Apache HTTP engine for use with the new Builder Config style.
 *
 * @deprecated This will be removed in a future release as the underlying default implementation of the
 *             {@link org.jboss.resteasy.client.jaxrs.ClientHttpEngine} will be replaced.
 */
@Deprecated(forRemoval = true, since = "6.2")
public class ApacheHttpClient43Engine extends ManualClosingApacheHttpClient43Engine {
    public ApacheHttpClient43Engine() {
        super();
    }

    public ApacheHttpClient43Engine(final HttpHost defaultProxy) {
        super(defaultProxy);
    }

    public ApacheHttpClient43Engine(final HttpClient httpClient) {
        super(httpClient);
    }

    public ApacheHttpClient43Engine(final HttpClient httpClient, final boolean closeHttpClient) {
        super(httpClient, closeHttpClient);
    }

    public ApacheHttpClient43Engine(final HttpClient httpClient, final HttpContextProvider httpContextProvider) {
        super(httpClient, httpContextProvider);
    }

}
