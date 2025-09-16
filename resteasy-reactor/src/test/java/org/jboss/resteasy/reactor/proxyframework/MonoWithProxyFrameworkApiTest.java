package org.jboss.resteasy.reactor.proxyframework;

import static org.jboss.resteasy.reactor.proxyframework.CustomResource.THE_CUSTOM_RESOURCE;

import java.net.URI;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import reactor.core.publisher.Mono;

@RestBootstrap(MonoWithProxyFrameworkApiTest.TestApplication.class)
public class MonoWithProxyFrameworkApiTest {

    @Path("api/v1")
    public interface RemoteCustomResource {

        @GET
        @Path("resource/custom")
        Mono<String> getCustomResource();
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(CustomResource.class);
        }
    }

    @Inject
    private ResteasyClient client;

    @Test
    public void givenRemoteServiceInterfaceAndWorkingRemoteServiceWhenProxyThenGenerateProxyWithMono(final URI uri) {
        final var proxy = client.target(uri).proxy(RemoteCustomResource.class);
        Assertions.assertEquals(THE_CUSTOM_RESOURCE, proxy.getCustomResource().block());

    }
}
