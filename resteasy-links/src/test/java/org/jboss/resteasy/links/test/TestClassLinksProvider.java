package org.jboss.resteasy.links.test;

import java.net.URI;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(TestClassLinksProvider.TestApplication.class)
public class TestClassLinksProvider {
    @Inject
    private Client client;

    @Test
    public void shouldGetBookClassLinks(final URI baseUri) {
        RESTServiceDiscovery restServiceDiscovery = client.target(baseUri)
                .queryParam("className", Book.class.getName())
                .request()
                .get(RESTServiceDiscovery.class);

        Assertions.assertEquals(2, restServiceDiscovery.size());
        Assertions.assertTrue(restServiceDiscovery.contains(new RESTServiceDiscovery.AtomLink(baseUri + "books", "list")));
        Assertions.assertTrue(restServiceDiscovery.contains(new RESTServiceDiscovery.AtomLink(baseUri + "books", "add")));
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(BookStore.class, ClassLinksProviderService.class);
        }
    }
}
