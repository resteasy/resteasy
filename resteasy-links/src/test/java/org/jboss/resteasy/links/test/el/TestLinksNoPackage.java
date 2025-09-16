package org.jboss.resteasy.links.test.el;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.jboss.resteasy.links.test.Book;
import org.jboss.resteasy.links.test.BookStoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(TestLinksNoPackage.TestApplication.class)
public class TestLinksNoPackage {

    private static String url;
    private static BookStoreService client;

    @BeforeAll
    public static void before(final ResteasyWebTarget target) {
        client = target.proxy(BookStoreService.class);
        url = target.getUri().toASCIIString();
    }

    @Test
    public void testELWorksWithoutPackage() throws Exception {
        Book book = client.getBookXML("foo");
        checkBookLinks1(book);
        book = client.getBookJSON("foo");
        checkBookLinks1(book);
    }

    private void checkBookLinks1(Book book) {
        Assertions.assertNotNull(book);
        Assertions.assertEquals("foo", book.getTitle());
        Assertions.assertEquals("bar", book.getAuthor());
        RESTServiceDiscovery links = book.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(1, links.size());
        // self
        AtomLink atomLink = links.getLinkForRel("self");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo", atomLink.getHref());
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(BookStoreNoPackage.class);
        }
    }
}
