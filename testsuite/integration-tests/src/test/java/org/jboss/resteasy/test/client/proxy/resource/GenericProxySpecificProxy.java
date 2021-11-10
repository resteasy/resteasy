package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Path;

@Path(value = "/say")
public interface GenericProxySpecificProxy extends GenericProxyBase<String> {

}
