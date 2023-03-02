package org.jboss.resteasy.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.net.URI;
import java.util.HashMap;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for ResteasyUriBuilderImpl class.
 * @tpSince RESTEasy 3.0.16
 */
public class ResteasyUriBuilderTest {

    private static final String URI_ERROR = "Uri function of ResteasyUriBuilderImpl should return builder itself.";
    private static final String URI_FRAGMENT_ERROR = "ResteasyUriBuilderImpl object encodes valid characters in the fragment uri";
    private static final String PATH_SEGMENT_PARSER_ERROR = "ResteasyUriBuilderImpl pathSegement parsing incorrect";
    private static final String PATH_PARSER_COUNT_ERROR = "ResteasyUriBuilderImpl pathSegement count incorrect";

    @Test
    public void pathSegementParserTest() {
        ResteasyUriBuilderImpl builder = new ResteasyUriBuilderImpl();
        HashMap<String, String> pathSegementsMap = new HashMap<>();

        {
            pathSegementsMap = builder.pathSegementParser(new String("/x/y/{path}?"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 1);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/x/y/{path}");
        }
        {
            pathSegementsMap = builder.pathSegementParser(
                    new String("/x/y/{path}?name={qval}"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 2);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/x/y/{path}");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("query"),
                    "name={qval}");
        }
        {
            pathSegementsMap = builder.pathSegementParser(
                    new String("/?DBquery#DBfragment"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 3);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("query"),
                    "DBquery");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("fragment"),
                    "DBfragment");
        }
        {
            pathSegementsMap = builder.pathSegementParser(
                    new String("/a/b/c=GB?objectClass?one"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 2);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/a/b/c=GB");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("query"),
                    "objectClass?one");
        }
        {
            pathSegementsMap = builder.pathSegementParser(
                    new String("/a/b/{string:[0-9 ?]+}"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 1);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/a/b/{string:[0-9 ?]+}");
        }
        {
            pathSegementsMap = builder.pathSegementParser(
                    new String("/a/b/{string:[0-9 ?]+}/c?a=x&b=ye/s?#"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 2);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/a/b/{string:[0-9 ?]+}/c");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("query"),
                    "a=x&b=ye/s?");
        }
        {
            pathSegementsMap = builder.pathSegementParser(
                    new String("/a/b/{string:[0-9 ?]+}/c?a=x&b=ye/s?#hello"));
            Assert.assertEquals(PATH_PARSER_COUNT_ERROR, pathSegementsMap.size(), 3);
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("path"),
                    "/a/b/{string:[0-9 ?]+}/c");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("query"),
                    "a=x&b=ye/s?");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathSegementsMap.get("fragment"),
                    "hello");
        }
    }

    /**
     * @tpTestDetails Uri method of ResteasyUriBuilderImpl object should return same object.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testParseHierarchicalUri() {
        ResteasyUriBuilderImpl builder = new ResteasyUriBuilderImpl();
        assertSame(URI_ERROR, builder, builder.uri("foo/bar:id"));
        builder = new ResteasyUriBuilderImpl();
        assertSame(URI_ERROR, builder, builder.uri("/bar:id"));
        builder = new ResteasyUriBuilderImpl();
        assertSame(URI_ERROR, builder, builder.uri("foo:bar/bar:id"));
        builder = new ResteasyUriBuilderImpl();
        assertSame(URI_ERROR, builder, builder.uri("foo:/bar"));
        builder = new ResteasyUriBuilderImpl();
        assertSame(URI_ERROR, builder, builder.uri("foo:bar"));
    }

    /**
     * @tpTestDetails ResteasyUriBuilderImpl object should not encode valid characters in the fragment uri
     * @tpTestDetails RESTEASY-1261
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriWithFragment() {
        UriBuilder builder = ResteasyUriBuilderImpl.fromTemplate("http://domain.com/path#fragment=with/allowed/special-chars");
        assertEquals(URI_FRAGMENT_ERROR, "http://domain.com/path#fragment=with/allowed/special-chars",
                builder.build().toString());

        builder = ResteasyUriBuilderImpl.fromTemplate("http://domain.com/path#fragment%with[forbidden}special<chars");
        assertEquals(URI_FRAGMENT_ERROR, "http://domain.com/path#fragment%25with%5Bforbidden%7Dspecial%3Cchars",
                builder.build().toString());
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
        assertEquals(errorMsg, baseAddr, oneUri.toString());

        uBuilder = new ResteasyUriBuilderImpl();
        URI twoUri = uBuilder
                .fromUri(baseAddr + "?foo=bar&foobar=qux")
                .replaceQueryParam("foo")
                .build();
        assertEquals(errorMsg, baseAddr + "?foobar=qux", twoUri.toString());
    }
}
