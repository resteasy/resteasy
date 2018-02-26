package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.test.resource.param.resource.CookieInjectionResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests cookie injection via @CookieParam and @Context header injection
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CookieInjectionTest {

    private final Logger logger = Logger.getLogger(CookieInjectionTest.class);
    static Client client;

    public interface CookieProxy {
        @Path("/param")
        @GET
        int param(@CookieParam("meaning") int value);

        @Path("/param")
        @GET
        int param(Cookie cookie);

        @Path("/expire")
        @GET
        Response expire();

        @Path("/expire1")
        @GET
        Response expire1();
    }

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(CookieInjectionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CookieInjectionResource.class);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CookieInjectionTest.class.getSimpleName());
    }

    private void _test(String path) {
        WebTarget target = client.target(generateURL(path));
        try {
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            MultivaluedMap<String, String> headers = response.getStringHeaders();
//            for (Map.Entry<String, List<String>> headerEntry : response.getStringHeaders().entrySet()) {
//                logger.debug(headerEntry.getKey() + ": " + headerEntry.getValue());
//            }
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Injection of the cookie into resource, request issued with client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt() {
        _test("/set");
        _test("/headers");
        _test("/headers/fromField");
        _test("/cookieparam");
        _test("/param");
        _test("/default");
    }

    private void _testExpire(String path) {
        WebTarget target = client.target(generateURL(path));
        try {
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String res = response.readEntity(String.class);
            MultivaluedMap<String, String> headers = response.getStringHeaders();
            Assert.assertTrue("Unexpected cookie expires:" + res, headers.get("Set-Cookie").contains(res));
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Cookie in the response header contains correct "Expires" attribute. Tested for cookie version 0 and 1.
     * See RESTEASY-1476 for details.
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testCookieExpire() {
        _testExpire("/expire");
        _testExpire("/expire1");
        _testExpire("/expired");
    }

    /**
     * @tpTestDetails Cookie in the response header contains correct "Expires" attribute. Tested for cookie version 0 and 1.
     * Proxy is used. See RESTEASY-1476 for details.
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testProxyExpire() {
        CookieProxy proxy = ProxyBuilder.builder(CookieProxy.class, client.target(generateURL("/"))).build();
        Response response = proxy.expire();
        String res = response.readEntity(String.class);
        MultivaluedMap<String, String> headers = response.getStringHeaders();
        Assert.assertTrue("Unexpected cookie expires:" + res, headers.get("Set-Cookie").contains(res));
        response.close();
    }
    /**
     * @tpTestDetails Injection of the cookie into resource, request issued with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() {
        {
            CookieProxy proxy = ProxyBuilder.builder(CookieProxy.class, client.target(generateURL("/"))).build();
            proxy.param(42);
        }
        {
            CookieProxy proxy = ProxyBuilder.builder(CookieProxy.class, client.target(generateURL("/"))).build();
            Cookie cookie = new Cookie("meaning", "42");
            proxy.param(cookie);
        }
    }
}
