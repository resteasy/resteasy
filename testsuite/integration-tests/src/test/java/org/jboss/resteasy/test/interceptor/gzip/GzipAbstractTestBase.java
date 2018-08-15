package org.jboss.resteasy.test.interceptor.gzip;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.interceptor.gzip.resource.GzipResource;
import org.jboss.resteasy.test.interceptor.gzip.resource.GzipInterface;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.PropertyPermission;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Abstract base class for gzip tests
 *
 * This abstract class provides basic test methods, Arquillian URL Resource and RESTEasy client
 *
 * This abstract class is extended by:
 *      AllowGzipOnServerAbstractTestBase
 *      NotAllowGzipOnServerAbstractTestBase
 */
@RunWith(Arquillian.class)
@RunAsClient
public abstract class GzipAbstractTestBase {

    private static final Logger LOG = LogManager.getLogger(GzipAbstractTestBase.class);

    /**
     * Allow gzip property
     */
    protected static final String PROPERTY_NAME = "resteasy.allowGzip";

    protected static final String WAR_WITH_PROVIDERS_FILE = "war_with_providers_file";
    protected static final String WAR_WITHOUT_PROVIDERS_FILE = "war_without_providers_file";


    /**
     * Prepare archive for the tests
     */
    protected static Archive<?> createWebArchive(String name, boolean addProvidersFileWithGzipInterceptors) {
        WebArchive war = TestUtil.prepareArchive(name);
        war = war.addClass(GzipInterface.class);
        war = war.addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new PropertyPermission("resteasy.allowGzip", "read")
        ), "permissions.xml");
        if (addProvidersFileWithGzipInterceptors) {
            war.addAsManifestResource(GzipAbstractTestBase.class.getPackage(), "GzipAbstractTest-javax.ws.rs.ext.Providers", "services/javax.ws.rs.ext.Providers");
        }
        return TestUtil.finishContainerPrepare(war, null, GzipResource.class);
    }

    private ResteasyClient client;

    /**
     * Perform gzip test
     *
     * @param deploymentUrl URL of the deployment to test
     * @param manuallyUseGzipOnClient manually register gzip interceptors on client side
     * @param assertAllowGzipOnServer if true, method asserts that resteasy.allowGzip == true on server side
     * @param assertAllowGzipOnClient if true, method asserts that client send gzip header in request
     * @param assertServerReturnGzip method asserts whether gzip encoding should be in header or should not
     * @throws Exception
     */
    protected void testNormalClient(URL deploymentUrl, boolean manuallyUseGzipOnClient, String assertAllowGzipOnServer, boolean assertAllowGzipOnClient,
                                    boolean assertServerReturnGzip) throws Exception {
        client = new ResteasyClientBuilder().build();

        if (manuallyUseGzipOnClient) {
            client.register(org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter.class);
            client.register(org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor.class);
            client.register(org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor.class);
        }

        try {
            // make http request
            String deploymentUrlString = deploymentUrl.toString();
            String url = deploymentUrlString + (!deploymentUrlString.endsWith("/") ? "/" : "") + "gzip/process";
            WebTarget base = client.target(url);
            String message2echo = "some statement";
            Response response = base.queryParam("name", message2echo).request().get();

            // echo URL
            LOG.info("URL: " + url);

            // check encoding in response
            String responseEncoding = response.getHeaderString("Content-Encoding");
            if (responseEncoding == null) {
                responseEncoding = "";
            }
            LOG.info("responseEncoding: " + responseEncoding);
            if (assertServerReturnGzip) {
                Assert.assertThat("wrong encoding of response", responseEncoding, containsString("gzip"));
            } else {
                Assert.assertThat("wrong encoding of response", responseEncoding, not(containsString("gzip")));
            }

            // read data from response
            String echo = response.readEntity(String.class);
            assertNotNull("Response doesn't have body", echo);

            // check resteasy.allowGzip property on server
            assertThat("Server doesn't have correct value of resteasy.allowGzip property", echo, startsWith(message2echo + " ___ -Dresteasy.allowGzip=" + assertAllowGzipOnServer));

            // check gzip request header
            if (assertAllowGzipOnClient) {
                assertThat("Server should receive request with gzip header", echo, endsWith("gzip_in_request_header_yes"));
            } else {
                assertThat("Server should not receive request with gzip header", echo, endsWith("gzip_in_request_header_no"));
            }

        } finally {
            client.close();
        }
    }

}
