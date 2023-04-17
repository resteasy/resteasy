package org.jboss.resteasy.reactor.proxyframework;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import reactor.core.publisher.Mono;

@Path("api/v1")
public class CustomResource {

    public static final String THE_CUSTOM_RESOURCE = "the custom resource";

    @GET
    @Path("resource/custom")
    public Mono<String> getCustomResource() {
        return Mono.just(THE_CUSTOM_RESOURCE);
    }
}
