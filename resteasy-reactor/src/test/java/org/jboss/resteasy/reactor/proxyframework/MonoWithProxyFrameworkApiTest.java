package org.jboss.resteasy.reactor.proxyframework;

import static org.jboss.resteasy.reactor.proxyframework.CustomResource.THE_CUSTOM_RESOURCE;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;
import reactor.core.publisher.Mono;

@RestBootstrap(CustomResource.class)
public class MonoWithProxyFrameworkApiTest {

    @Path("api/v1")
    public interface RemoteCustomResource {

        @GET
        @Path("resource/custom")
        Mono<String> getCustomResource();
    }

    @RestResource
    private ResteasyClient client;

    @Test
    public void givenRemoteServiceInterfaceAndWorkingRemoteServiceWhenProxyThenGenerateProxyWithMono(
            @RestResource final URI uri) {
        final var proxy = client.target(uri).proxy(RemoteCustomResource.class);
        Assertions.assertEquals(THE_CUSTOM_RESOURCE, proxy.getCustomResource().block());

    }
}
