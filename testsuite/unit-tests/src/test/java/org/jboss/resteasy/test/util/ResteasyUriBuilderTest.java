package org.jboss.resteasy.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.net.URI;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for ResteasyUriBuilderImpl class.
 * @tpSince RESTEasy 3.0.16
 */
public class ResteasyUriBuilderTest {

    private static final String URI_ERROR = "Uri function of ResteasyUriBuilderImpl should return builder itself.";
    private static final String URI_FRAGMENT_ERROR = "ResteasyUriBuilderImpl object encodes valid characters in the fragment uri";

    /**
     * @tpTestDetails Uri method of ResteasyUriBuilderImpl object should return same object.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testParseHierarchicalUri() {
        ResteasyUriBuilderImpl builder = new ResteasyUriBuilderImpl();
        assertSame(builder, builder.uri("foo/bar:id"), URI_ERROR);
        builder = new ResteasyUriBuilderImpl();
        assertSame(builder, builder.uri("/bar:id"), URI_ERROR);
        builder = new ResteasyUriBuilderImpl();
        assertSame(builder, builder.uri("foo:bar/bar:id"), URI_ERROR);
        builder = new ResteasyUriBuilderImpl();
        assertSame(builder, builder.uri("foo:/bar"), URI_ERROR);
        builder = new ResteasyUriBuilderImpl();
        assertSame(builder, builder.uri("foo:bar"), URI_ERROR);
    }

    /**
     * @tpTestDetails ResteasyUriBuilderImpl object should not encode valid characters in the fragment uri
     * @tpTestDetails RESTEASY-1261
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriWithFragment() {
        UriBuilder builder = ResteasyUriBuilderImpl.fromTemplate("http://domain.com/path#fragment=with/allowed/special-chars");
        assertEquals("http://domain.com/path#fragment=with/allowed/special-chars",
                builder.build().toString(), URI_FRAGMENT_ERROR);

        builder = ResteasyUriBuilderImpl.fromTemplate("http://domain.com/path#fragment%with[forbidden}special<chars");
        assertEquals("http://domain.com/path#fragment%25with%5Bforbidden%7Dspecial%3Cchars",
                builder.build().toString(), URI_FRAGMENT_ERROR);
    }

    @Test
    public void testReplaceQueryParam() {
        String errorMsg = "Quary param incorrectly replaced";
        String baseAddr = "http://example.com/api";
        UriBuilder uBuilder = new ResteasyUriBuilderImpl();
        URI oneUri = uBuilder
                .fromUri(baseAddr + "?foo=bar")
                .replaceQueryParam("foo")
                .build();
        assertEquals(baseAddr, oneUri.toString(), errorMsg);

        uBuilder = new ResteasyUriBuilderImpl();
        URI twoUri = uBuilder
                .fromUri(baseAddr + "?foo=bar&foobar=qux")
                .replaceQueryParam("foo")
                .build();
        assertEquals(baseAddr + "?foobar=qux", twoUri.toString(), errorMsg);
    }
}
