package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.test.resource.path.resource.TrailingSlashResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4698
 */
@RunWith(Arquillian.class)
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
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath());
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath(true));
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath(false));
        Assert.assertEquals(ERROR_MSG, uri, ruri.getAbsolutePath());
        Assert.assertEquals(ERROR_MSG, uri, ruri.getBaseUri().resolve(ruri.getPath(false)));
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
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath());
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath(true));
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath(false));
        URI newUri;
        if (base.toString().endsWith("/")) {
            newUri = new URI(base.toString().substring(0, base.toString().length() - 1) + path);
        } else {
            newUri = new URI(base.toString() + path);
        }
        Assert.assertEquals(ERROR_MSG, newUri, ruri.getAbsolutePath());
    }

    /**
     * @tpTestDetails ResteasyUriInfo is based on queryString and contextPath.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void threeArgConstructorTest() throws Exception {
        doThreeArgConstructorTest("http://localhost/abc", "/abc");
        doThreeArgConstructorTest("http://localhost/abc/", "/abc/");
    }

    void doThreeArgConstructorTest(String s, String path) throws URISyntaxException {
        ResteasyUriInfo ruri = new ResteasyUriInfo(s, "", "");
        URI uri = new URI(s);
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath());
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath(true));
        Assert.assertEquals(ERROR_MSG, path, ruri.getPath(false));
        Assert.assertEquals(ERROR_MSG, uri, ruri.getAbsolutePath());
        Assert.assertEquals(ERROR_MSG, uri, ruri.getBaseUri().resolve(ruri.getPath(false)));
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
        Assert.assertEquals(ERROR_MSG, "/test", response.readEntity(String.class));
        client.close();
    }

    /**
     * @tpTestDetails Slash at the end of URI
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testSlash() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test/"));
        Response response = target.request().get();
        Assert.assertEquals(ERROR_MSG, "/test/", response.readEntity(String.class));
        client.close();
    }
}
