package org.jboss.resteasy.test.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.ClientResponseRedirectIntf;
import org.jboss.resteasy.test.client.resource.ClientResponseRedirectResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.PropertyPermission;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientResponseRedirectTest extends ClientTestBase{


    protected static final Logger logger = LogManager.getLogger(ClientResponseRedirectTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientResponseRedirectTest.class.getSimpleName());
        war.addClass(ClientTestBase.class);
        // Use of PortProviderutil in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new PropertyPermission("node", "read"),
                new PropertyPermission("ipv6", "read"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new PropertyPermission("org.jboss.resteasy.port", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ClientResponseRedirectResource.class, PortProviderUtil.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Tests redirection of the request using ProxyBuilder client
     * @tpPassCrit The header 'location' contains the redirected target
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRedirectProxyBuilder() throws Exception {
        testRedirect(ProxyBuilder.builder(ClientResponseRedirectIntf.class, client.target(generateURL(""))).build().get());
    }

    /**
     * @tpTestDetails Tests redirection of the request using Client Webtarget request
     * @tpPassCrit The header 'location' contains the redirected target
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRedirectClientTargetRequest() throws Exception {
        testRedirect(client.target(generateURL("/redirect")).request().get());
    }

    /**
     * @tpTestDetails Tests redirection of the request using HttpUrlConnection
     * @tpPassCrit The header 'location' contains the redirected target
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRedirectHttpUrlConnection() throws Exception {
        URL url = PortProviderUtil.createURL("/redirect", ClientResponseRedirectTest.class.getSimpleName());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("GET");
//        for (Object name : conn.getHeaderFields().keySet()) {
//            logger.debug(name);
//        }
        Assert.assertEquals("The response from the server was: " + conn.getResponseCode(),
                HttpResponseCodes.SC_SEE_OTHER, conn.getResponseCode());
    }

    @SuppressWarnings(value = "unchecked")
    private void testRedirect(Response response) {
        MultivaluedMap<String, Object> headers = response.getHeaders();
//        logger.debug("size: " + headers.size());
//        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
//            logger.debug(entry.getKey() + ":" + entry.getValue().get(0));
//        }
        Assert.assertTrue(headers.getFirst("location").toString().equalsIgnoreCase(PortProviderUtil.generateURL("/redirect/data", ClientResponseRedirectTest.class.getSimpleName())));
    }

}
