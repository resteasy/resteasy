package org.jboss.resteasy.test.cookies;

import java.util.Date;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.NewCookie.SameSite;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.cookies.NewCookie6265;
import org.jboss.resteasy.test.cookies.resource.TestCookie6265Resource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.common.Assert;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.20
 * @tpTestCaseDetails Test client error caused by bad media type
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class Cookie6265Test {
    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Cookie6265Test.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        return TestUtil.finishContainerPrepare(war, null, TestCookie6265Resource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Cookie6265Test.class.getSimpleName());
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails There are two methods that match path, but only one matches Accept.
     * @tpSince RESTEasy 3.0.20
     */

    @Test
    public void testCookies6265() throws Exception {

        Response response = client.target(generateURL("/getNewCookies/6265")).request().get();
        Assert.assertTrue(response.getStatus() == 200);
        Map<String, NewCookie> cookies = response.getCookies();
        NewCookie6265 newCookie6265a = (NewCookie6265) cookies.get("name6265a");
        Assert.assertTrue("value1".equals(newCookie6265a.getValue()));
        Assert.assertTrue(NewCookie6265.NO_VERSION == newCookie6265a.getVersion());
        Assert.assertTrue("/path1".equals(newCookie6265a.getPath()));
        Assert.assertTrue("domain1".equals(newCookie6265a.getDomain()));
        Assert.assertTrue(null == newCookie6265a.getComment());
        Assert.assertTrue(23 == newCookie6265a.getMaxAge());
        Assert.assertTrue(new Date(3, 5, 7).equals(newCookie6265a.getExpiry()));
        Assert.assertFalse(newCookie6265a.isSecure());
        Assert.assertTrue(newCookie6265a.isHttpOnly());
        Assert.assertTrue(SameSite.LAX.equals(newCookie6265a.getSameSite()));
        Assert.assertTrue(newCookie6265a.getExtensions().size() == 2);
        Assert.assertTrue(newCookie6265a.getExtensions().contains("a=b"));
        Assert.assertTrue(newCookie6265a.getExtensions().contains("c"));

        NewCookie newCookie6265b = cookies.get("name6265b");
        Assert.assertTrue("value2".equals(newCookie6265b.getValue()));
        Assert.assertTrue(NewCookie6265.NO_VERSION == newCookie6265b.getVersion());
        Assert.assertTrue("/path2".equals(newCookie6265b.getPath()));
        Assert.assertTrue("domain2".equals(newCookie6265b.getDomain()));
        Assert.assertTrue(null == newCookie6265b.getComment());
        Assert.assertTrue(29 == newCookie6265b.getMaxAge());
        Assert.assertTrue(new Date(5, 7, 11).equals(newCookie6265b.getExpiry()));
        Assert.assertTrue(newCookie6265b.isSecure());
        Assert.assertFalse(newCookie6265b.isHttpOnly());
        Assert.assertTrue(SameSite.STRICT.equals(newCookie6265b.getSameSite()));

        // Send Cookie back to server
        response = client.target(generateURL("/checkCookies/cookies/6265")).request()
                .cookie(newCookie6265a)
                .cookie(newCookie6265b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));

        response = client.target(generateURL("/checkCookies/string/6265")).request()
                .cookie(newCookie6265a)
                .cookie(newCookie6265b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));
    }

    @Test
    public void testCookies2109() throws Exception {

        Response response = client.target(generateURL("/getNewCookies/2109")).request().get();
        Assert.assertTrue(response.getStatus() == 200);
        Map<String, NewCookie> cookies = response.getCookies();
        NewCookie newCookie2109a = cookies.get("name2109a");
        Assert.assertTrue("value1".equals(newCookie2109a.getValue()));
        Assert.assertTrue(17 == newCookie2109a.getVersion());
        Assert.assertTrue("/path1".equals(newCookie2109a.getPath()));
        Assert.assertTrue("domain1".equals(newCookie2109a.getDomain()));
        Assert.assertTrue("comment1".equals(newCookie2109a.getComment()));
        Assert.assertTrue(23 == newCookie2109a.getMaxAge());
        Assert.assertTrue(new Date(3, 5, 7).equals(newCookie2109a.getExpiry()));
        Assert.assertFalse(newCookie2109a.isSecure());
        Assert.assertTrue(newCookie2109a.isHttpOnly());
        Assert.assertTrue(SameSite.LAX.equals(newCookie2109a.getSameSite()));

        NewCookie newCookie2109b = cookies.get("name2109b");
        Assert.assertTrue("value2".equals(newCookie2109b.getValue()));
        Assert.assertTrue(19 == newCookie2109b.getVersion());
        Assert.assertTrue("/path2".equals(newCookie2109b.getPath()));
        Assert.assertTrue("domain2".equals(newCookie2109b.getDomain()));
        Assert.assertTrue("comment2".equals(newCookie2109b.getComment()));
        Assert.assertTrue(29 == newCookie2109b.getMaxAge());
        Assert.assertTrue(new Date(5, 7, 11).equals(newCookie2109b.getExpiry()));
        Assert.assertTrue(newCookie2109b.isSecure());
        Assert.assertFalse(newCookie2109b.isHttpOnly());
        Assert.assertTrue(SameSite.STRICT.equals(newCookie2109b.getSameSite()));

        // Send Cookie back to server
        response = client.target(generateURL("/checkCookies/cookies/2109")).request()
                .cookie(newCookie2109a)
                .cookie(newCookie2109b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));

        response = client.target(generateURL("/checkCookies/string/2109")).request()
                .cookie(newCookie2109a)
                .cookie(newCookie2109b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));
    }
}
