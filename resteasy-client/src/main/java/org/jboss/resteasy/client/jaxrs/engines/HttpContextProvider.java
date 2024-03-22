package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.http.protocol.HttpContext;

@Deprecated(forRemoval = true, since = "6.2")
public interface HttpContextProvider {
    HttpContext getContext();
}
