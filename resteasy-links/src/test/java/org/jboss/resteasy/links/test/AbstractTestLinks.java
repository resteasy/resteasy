/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.links.test;

import java.util.List;

import jakarta.inject.Inject;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

abstract class AbstractTestLinks {

    @Inject
    private ResteasyWebTarget webTarget;

    @ParameterizedTest
    @ValueSource(strings = { "json", "xml" })
    public void links(final String type) throws Exception {
        final BookStoreService client = webTarget.proxy(BookStoreService.class);
        final String url = webTarget.getUri().toASCIIString();
        Book book = null;
        switch (type) {
            case "xml":
                book = client.getBookXML("foo");
                break;
            case "json":
                book = client.getBookJSON("foo");
                break;
        }
        Assertions.assertNotNull(book);
        Assertions.assertEquals("foo", book.getTitle());
        Assertions.assertEquals("bar", book.getAuthor());
        RESTServiceDiscovery links = book.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(7, links.size());
        // self
        AtomLink atomLink = links.getLinkForRel("self");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo", atomLink.getHref());
        // update
        atomLink = links.getLinkForRel("update");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo", atomLink.getHref());
        // remove
        atomLink = links.getLinkForRel("remove");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo", atomLink.getHref());
        // list
        atomLink = links.getLinkForRel("list");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "books", atomLink.getHref());
        // add
        atomLink = links.getLinkForRel("add");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "books", atomLink.getHref());
        // comments
        atomLink = links.getLinkForRel("comments");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comments", atomLink.getHref());
        // comment collection
        atomLink = links.getLinkForRel("comment-collection");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment-collection", atomLink.getHref());
    }

    @ParameterizedTest
    @ValueSource(strings = { "json", "xml" })
    public void comments(final String type) throws Exception {
        final BookStoreService client = webTarget.proxy(BookStoreService.class);
        final String url = webTarget.getUri().toASCIIString();
        List<Comment> comments = null;
        switch (type) {
            case "xml":
                comments = client.getBookCommentsXML("foo");
                break;
            case "json":
                comments = client.getBookCommentsJSON("foo");
                break;
        }
        Assertions.assertNotNull(comments);
        Assertions.assertFalse(comments.isEmpty());
        final var comment = comments.get(0);
        Assertions.assertNotNull(comment);
        Assertions.assertEquals(Integer.toString(0), comment.getId());
        RESTServiceDiscovery links = comment.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(6, links.size());
        // self
        AtomLink atomLink = links.getLinkForRel("self");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment/0", atomLink.getHref());
        // update
        atomLink = links.getLinkForRel("update");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment/0", atomLink.getHref());
        // remove
        atomLink = links.getLinkForRel("remove");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment/0", atomLink.getHref());
        // list
        atomLink = links.getLinkForRel("list");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comments", atomLink.getHref());
        // add
        atomLink = links.getLinkForRel("add");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comments", atomLink.getHref());
        // collection
        atomLink = links.getLinkForRel("collection");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment-collection", atomLink.getHref());
    }

    @ParameterizedTest
    @ValueSource(strings = { "json", "xml" })
    public void facadeLinks(String type) throws Exception {
        final BookStoreService client = webTarget.proxy(BookStoreService.class);
        final String url = webTarget.getUri().toASCIIString();
        ScrollableCollection comments = null;
        switch (type) {
            case "xml":
                comments = client.getScrollableCommentsXML("foo", "book");
                break;
            case "json":
                comments = client.getScrollableCommentsJSON("foo", "book");
                break;

        }
        Assertions.assertNotNull(comments);
        RESTServiceDiscovery links = comments.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(5, links.size());
        // list
        AtomLink atomLink = links.getLinkForRel("list");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comments", atomLink.getHref());
        // add
        atomLink = links.getLinkForRel("add");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comments", atomLink.getHref());
        // comment collection
        atomLink = links.getLinkForRel("collection");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment-collection", atomLink.getHref());
        // next
        atomLink = links.getLinkForRel("next");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "book/foo/comment-collection;query=book?start=1&limit=1", atomLink.getHref());
        // home
        atomLink = links.getLinkForRel("home");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url, atomLink.getHref());
    }
}
