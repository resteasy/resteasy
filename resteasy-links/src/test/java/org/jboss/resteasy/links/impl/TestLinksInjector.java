package org.jboss.resteasy.links.impl;

import java.net.URI;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.junit.Assert;
import org.junit.Test;

public class TestLinksInjector {

    private final LinksInjector injector = new LinksInjector();

    @Test
    public void shouldNotInjectIfLinksFieldDoesNotExist() {
        injector.inject(new EntityWithoutLinks(), new RESTServiceDiscovery()); // Nothing should happen
    }

    @Test
    public void shouldInjectLinks() {
        EntityWithLinks entity = new EntityWithLinks(null);

        RESTServiceDiscovery links = new RESTServiceDiscovery();
        links.addLink(URI.create("http://example.com"), "example");

        injector.inject(entity, links);

        Assert.assertEquals(links, entity.getRestServiceDiscovery());
    }

    @Test
    public void shouldAppendLinks() {
        RESTServiceDiscovery initialLinks = new RESTServiceDiscovery();
        initialLinks.addLink(URI.create("https://resteasy.github.io"), "resteasy");
        EntityWithLinks entity = new EntityWithLinks(initialLinks);

        RESTServiceDiscovery newLinks = new RESTServiceDiscovery();
        newLinks.addLink(URI.create("http://example.com"), "example");

        injector.inject(entity, newLinks);

        Assert.assertEquals(2, entity.getRestServiceDiscovery().size());
        Assert.assertEquals("https://resteasy.github.io",
                entity.getRestServiceDiscovery().getLinkForRel("resteasy").getHref());
        Assert.assertEquals("http://example.com", entity.getRestServiceDiscovery().getLinkForRel("example").getHref());
    }

    private static final class EntityWithLinks {
        private RESTServiceDiscovery restServiceDiscovery;

        EntityWithLinks(final RESTServiceDiscovery restServiceDiscovery) {
            this.restServiceDiscovery = restServiceDiscovery;
        }

        public RESTServiceDiscovery getRestServiceDiscovery() {
            return restServiceDiscovery;
        }
    }

    private static final class EntityWithoutLinks {}
}
