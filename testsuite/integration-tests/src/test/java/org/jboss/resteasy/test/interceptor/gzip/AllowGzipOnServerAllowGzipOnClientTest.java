package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class AllowGzipOnServerAllowGzipOnClientTest extends AllowGzipOnServerAbstractTestBase {

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
    @OperateOnDeployment(WAR_WITHOUT_PROVIDERS_FILE)
    public void allowGzipOnServerAllowGzipOnClientTest() throws Exception {
        testNormalClient(new URL(gzipServerBaseUrl + "/" + WAR_WITHOUT_PROVIDERS_FILE), false, "true", true, true);
    }

}
