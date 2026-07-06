package org.jboss.resteasy.links.test;

import java.net.URI;

import jakarta.ws.rs.client.Client;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

@RestBootstrap({ BookStore.class, ClassLinksProviderService.class })
public class TestClassLinksProvider {
    @RestResource
    private Client client;

    @Test
    public void shouldGetBookClassLinks(@RestResource final URI baseUri) {
        RESTServiceDiscovery restServiceDiscovery = client.target(baseUri)
                .queryParam("className", Book.class.getName())
                .request()
                .get(RESTServiceDiscovery.class);

        Assertions.assertEquals(2, restServiceDiscovery.size());
        Assertions.assertTrue(restServiceDiscovery.contains(new RESTServiceDiscovery.AtomLink(baseUri + "books", "list")));
        Assertions.assertTrue(restServiceDiscovery.contains(new RESTServiceDiscovery.AtomLink(baseUri + "books", "add")));
    }
}
