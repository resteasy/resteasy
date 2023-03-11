package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(500)
// reverse order
public class ResponseContainerResponseFilter extends ResponseContainerTemplateFilter {

    public void hasEntity() {
        boolean has = responseContext.hasEntity();
        setEntity(String.valueOf(has));
    }
}
