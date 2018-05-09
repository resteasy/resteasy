package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class NotAllowGzipOnServerAllowGzipOnClientTest extends GzipAbstractTest {

    @BeforeClass
    public static void init() {
        System.setProperty(PROPERTY_NAME, Boolean.TRUE.toString());
    }

    @AfterClass
    public static void clean() {
        System.clearProperty(PROPERTY_NAME);
    }

    /**
     * @tpTestDetails gzip is disabled on server
     *                gzip is allowed on client by resteasy.allowGzip system property
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_without_providers_file")
    public void noProvidersFileOnServer() throws Exception {
        testNormalClient(false, "null", true, false);
    }

    /**
     * @tpTestDetails gzip is enabled on server by javax.ws.rs.ext.Providers file in deployment
     *                gzip is allowed on client by resteasy.allowGzip system property
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_with_providers_file")
    public void providersFileOnServer() throws Exception {
        testNormalClient(false, "null", true, true);
    }

}
