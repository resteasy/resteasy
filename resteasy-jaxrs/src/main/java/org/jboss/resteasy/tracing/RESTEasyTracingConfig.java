package org.jboss.resteasy.tracing;

public enum RESTEasyTracingConfig {

    /**
     * Tracing support is completely disabled.
     */
    OFF,
    /**
     * Tracing support is in stand-by mode. Waiting for a request header
     * {@link RESTEasyTracingLogger#HEADER_ACCEPT} existence.
     */
    ON_DEMAND,
    /**
     * Tracing support is enabled for every request.
     */
    ALL

}
