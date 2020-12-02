package org.jboss.resteasy.test.microprofile.restclient.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Stateless
@Path("test")
public class RestClientProxyRedeployResource {

    @Inject
    @RestClient
    private RestClientProxyRedeployRemoteService testService;

    @GET
    @Path("1")
    public String test() {
        try {
            testService.get();
        } catch (Exception e) {
            return "ERROR";
        }
        return "OK";
    }
}
