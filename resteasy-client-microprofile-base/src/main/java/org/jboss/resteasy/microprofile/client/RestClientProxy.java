package org.jboss.resteasy.microprofile.client;

import jakarta.ws.rs.client.Client;

/**
 * This interface is implemented by every proxy created by {@link RestClientBuilderImpl}.
 */
public interface RestClientProxy {

    /**
     * Release/close all associated resources, including the underlying {@link Client} instance.
     */
    void close();

    /**
     *
     * @return the underlying {@link Client} instance
     */
    Client getClient();

}
