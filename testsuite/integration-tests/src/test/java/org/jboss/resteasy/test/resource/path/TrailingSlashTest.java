package org.jboss.resteasy.test.resource.path;

import java.net.URI;
import java.net.URISyntaxException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.test.resource.path.resource.TrailingSlashResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4698
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class TrailingSlashTest {
    private static final String ERROR_MSG = "ResteasyUriInfo parsed slash wrongly";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResteasyTrailingSlashTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TrailingSlashResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResteasyTrailingSlashTest.class.getSimpleName());
    }

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
    public void threeArgConstructorTest() throws Exception {
        doTwoArgConstructorTest("http://localhost/abc", "/abc");
        doTwoArgConstructorTest("http://localhost/abc/", "/abc/");
    }

    void doTwoArgConstructorTest(String s, String path) throws URISyntaxException {
        ResteasyUriInfo ruri = new ResteasyUriInfo(s, "");
        URI uri = new URI(s);
        Assertions.assertEquals(path, ruri.getPath(), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(true), ERROR_MSG);
        Assertions.assertEquals(path, ruri.getPath(false), ERROR_MSG);
        Assertions.assertEquals(uri, ruri.getAbsolutePath(), ERROR_MSG);
        Assertions.assertEquals(uri, ruri.getBaseUri().resolve(ruri.getPath(false)),
                ERROR_MSG);
    }

    /**
     * @tpTestDetails No slash at the end of URI
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testNoSlash() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test"));
        Response response = target.request().get();
        Assertions.assertEquals("/test", response.readEntity(String.class), ERROR_MSG);
        client.close();
    }

    /**
     * @tpTestDetails Slash at the end of URI
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testSlash() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test/"));
        Response response = target.request().get();
        Assertions.assertEquals("/test/", response.readEntity(String.class), ERROR_MSG);
        client.close();
    }
}
