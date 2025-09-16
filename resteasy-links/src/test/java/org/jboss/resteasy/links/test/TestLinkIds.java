package org.jboss.resteasy.links.test;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(TestLinkIds.TestApplication.class)
public class TestLinkIds {

    private static IDServiceTest client;
    private static String url;

    @BeforeAll
    public static void beforeClass(final ResteasyWebTarget webTarget) throws Exception {
        client = webTarget.proxy(IDServiceTest.class);
        url = webTarget.getUri().toASCIIString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
    }

    @Test
    public void testResourceId() throws Exception {
        IdBook book = client.getResourceIdBook("foo");
        checkBook(book, "/resource-id/book/foo");
    }

    @Test
    public void testResourceIds() throws Exception {
        IdBook book = client.getResourceIdsBook("foo", "bar");
        checkBook(book, "/resource-ids/book/foo/bar");
    }

    @Test
    public void testResourceIdMethod() throws Exception {
        IdBook book = client.getResourceIdMethodBook("foo");
        checkBook(book, "/resource-id-method/book/foo");
    }

    @Test
    public void testResourceIdsMethod() throws Exception {
        IdBook book = client.getResourceIdsMethodBook("foo", "bar");
        checkBook(book, "/resource-ids-method/book/foo/bar");
    }

    @Test
    public void testXmlId() throws Exception {
        IdBook book = client.getXmlIdBook("foo");
        checkBook(book, "/xml-id/book/foo");
    }

    @Test
    public void testJpaId() throws Exception {
        IdBook book = client.getJpaIdBook("foo");
        checkBook(book, "/jpa-id/book/foo");
    }

    private void checkBook(IdBook book, String relativeUrl) {
        Assertions.assertNotNull(book);
        RESTServiceDiscovery links = book.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(1, links.size());
        AtomLink link = links.get(0);
        Assertions.assertEquals("self", link.getRel());
        Assertions.assertEquals(url + relativeUrl, link.getHref());
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(IDServiceTestBean.class);
        }
    }
}
