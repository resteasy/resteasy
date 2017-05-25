package org.jboss.resteasy.client.jaxrs;

import org.apache.http.impl.client.HttpClientBuilder;

/**
 * This interface provides a means for a user to provide a custom (apache 4.5+)
 * HttpClientBuilder that can be used in the process of generating a ClientHttpEngine
 * object.  In addition the user has the option of using the values set in
 * ResteasyClientBuilder for generation of configuration elements of the ClientHttpEngine.
 *
 * User: rsearls
 * Date: 5/5/17
 */
public interface HttpClientEngineBuilder {
    /**
     *
     * @param httpClient
     */
    public void setHttpClientBuilder (HttpClientBuilder httpClient);

    /**
     *
     * @param that
     * @return
     */
    public ClientHttpEngine initClientHttpEngine(ResteasyClientBuilder that);
}
