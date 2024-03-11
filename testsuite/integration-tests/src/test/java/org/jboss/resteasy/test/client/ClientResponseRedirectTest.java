package org.jboss.resteasy.test.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.ClientResponseRedirectIntf;
import org.jboss.resteasy.test.client.resource.ClientResponseRedirectResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientResponseRedirectTest extends ClientTestBase {

    protected static final Logger logger = Logger.getLogger(ClientResponseRedirectTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientResponseRedirectTest.class.getSimpleName());
        war.addClass(ClientTestBase.class);
        // Use of PortProviderutil in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new PropertyPermission("node", "read"),
                new PropertyPermission("ipv6", "read"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ClientResponseRedirectResource.class, PortProviderUtil.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        Assertions.assertEquals(HttpResponseCodes.SC_SEE_OTHER, conn.getResponseCode(),
                "The response from the server was: " + conn.getResponseCode());
    }

    @SuppressWarnings(value = "unchecked")
    private void testRedirect(Response response) {
        MultivaluedMap<String, Object> headers = response.getHeaders();
        //        logger.debug("size: " + headers.size());
        //        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
        //            logger.debug(entry.getKey() + ":" + entry.getValue().get(0));
        //        }
        Assertions.assertTrue(headers.getFirst("location").toString().equalsIgnoreCase(
                PortProviderUtil.generateURL("/redirect/data", ClientResponseRedirectTest.class.getSimpleName())));
    }

}
