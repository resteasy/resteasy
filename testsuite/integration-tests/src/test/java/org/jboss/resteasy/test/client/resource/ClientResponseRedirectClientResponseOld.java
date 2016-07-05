package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Old client test)

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/redirect")
public interface ClientResponseRedirectClientResponseOld {
    @GET
    ClientResponse get();
}
