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
public class AllowGzipOnServerAllowGzipOnClientTest extends GzipAbstractTest {

    @BeforeClass
    public static void init() {
        System.setProperty(PROPERTY_NAME, Boolean.TRUE.toString());
    }

    @AfterClass
    public static void clean() {
        System.clearProperty(PROPERTY_NAME);
    }

    /**
     * @tpTestDetails gzip is allowed on both server and client by resteasy.allowGzip system property
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_without_providers_file")
    public void allowGzipOnServerAllowGzipOnClientTest() throws Exception {
        testNormalClient(false, "true", true, true);
    }

}
