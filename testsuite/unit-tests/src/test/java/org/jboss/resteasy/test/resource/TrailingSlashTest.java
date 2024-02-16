package org.jboss.resteasy.test.resource;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4698
 */
public class TrailingSlashTest {
    private static final String ERROR_MSG = "ResteasyUriInfo parsed slash wrongly";

    /**
     * @tpTestDetails Simple construction of ResteasyUriInfo.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void oneArgConstructorTest() throws Exception {
        doOneArgConstructorTest(new URI("http://localhost/abc"), "/abc");
        doOneArgConstructorTest(new URI("http://localhost/abc/"), "/abc/");
    }

    void doOneArgConstructorTest(URI uri, String path) {
        ResteasyUriInfo ruri = new ResteasyUriInfo(uri);
        Assertions.assertEquals(path, ruri.getPath(), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(true), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(false), ERROR_MSG);
        Assertions.assertEquals(uri, ruri.getAbsolutePath(), ERROR_MSG);
        Assertions.assertEquals(uri, ruri.getBaseUri().resolve(ruri.getPath(false)),
                ERROR_MSG);
    }

    /**
     * @tpTestDetails ResteasyUriInfo is based of two URIs.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void twoArgConstructorTest() throws Exception {
        doTwoArgConstructorTest(new URI("http://localhost/abc"), new URI("xyz"), "/xyz");
        doTwoArgConstructorTest(new URI("http://localhost/abc"), new URI("xyz/"), "/xyz/");
    }

    void doTwoArgConstructorTest(URI base, URI relative, String path) throws URISyntaxException {
        ResteasyUriInfo ruri = new ResteasyUriInfo(base, relative);
        Assertions.assertEquals(path, ruri.getPath(), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(true), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(false), ERROR_MSG);
        URI newUri;
        if (base.toString().endsWith("/")) {
            newUri = new URI(base.toString().substring(0, base.toString().length() - 1) + path);
        } else {
            newUri = new URI(base.toString() + path);
        }
        Assertions.assertEquals(newUri, ruri.getAbsolutePath(), ERROR_MSG);
    }

    /**
     * @tpTestDetails ResteasyUriInfo is based on queryString and contextPath.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void twoStringArgConstructorTest() throws Exception {
        doTwoStringArgConstructorTest("http://localhost/abc", "/abc");
        doTwoStringArgConstructorTest("http://localhost/abc/", "/abc/");
    }

    void doTwoStringArgConstructorTest(String s, String path) throws URISyntaxException {
        ResteasyUriInfo ruri = new ResteasyUriInfo(s, "");
        URI uri = new URI(s);
        Assertions.assertEquals(path, ruri.getPath(), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(true), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(false), ERROR_MSG);
        Assertions.assertEquals(uri, ruri.getAbsolutePath(), ERROR_MSG);
        Assertions.assertEquals(uri, ruri.getBaseUri().resolve(ruri.getPath(false)),
                ERROR_MSG);
    }
}
