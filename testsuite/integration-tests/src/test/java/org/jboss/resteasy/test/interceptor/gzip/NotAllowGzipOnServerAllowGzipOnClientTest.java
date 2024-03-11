package org.jboss.resteasy.test.interceptor.gzip;

import java.net.URL;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class NotAllowGzipOnServerAllowGzipOnClientTest extends NotAllowGzipOnServerAbstractTestBase {

    @BeforeAll
    public static void init() {
        System.setProperty(PROPERTY_NAME, Boolean.TRUE.toString());
    }

    @AfterAll
    public static void clean() {
        System.clearProperty(PROPERTY_NAME);
    }

    @ArquillianResource
    private URL deploymentBaseUrl;

    /**
     * @tpTestDetails gzip is disabled on server
     *                gzip is allowed on client by resteasy.allowGzip system property
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITHOUT_PROVIDERS_FILE)
    public void noProvidersFileOnServer() throws Exception {
        testNormalClient(deploymentBaseUrl, false, "null", true, false);
    }

    /**
     * @tpTestDetails gzip is enabled on server by jakarta.ws.rs.ext.Providers file in deployment
     *                gzip is allowed on client by resteasy.allowGzip system property
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITH_PROVIDERS_FILE)
    public void providersFileOnServer() throws Exception {
        testNormalClient(deploymentBaseUrl, false, "null", true, true);
    }

}
