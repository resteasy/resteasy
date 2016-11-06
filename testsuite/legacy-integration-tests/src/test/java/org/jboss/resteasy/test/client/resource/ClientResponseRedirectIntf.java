package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/redirect")
public interface ClientResponseRedirectIntf {
    @GET
    Response get();
}
