package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.test.client.proxy.ProxyPathParamRegexTest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class ProxyPathParamRegexResource implements ProxyPathParamRegexTest.RegexInterface {
    @GET
    @Path("/{path}/{string:[a-z]?}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPath(@PathParam("path") String path, @PathParam("string") @Encoded String string) {
        String responseString = path + string;
        return responseString;
    }
}