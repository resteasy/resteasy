package org.jboss.resteasy.test.interceptor.gzip;

import java.net.URL;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
@Disabled("RESTEASY-3451")
public class AllowGzipOnServerAllowGzipOnClientTest extends AllowGzipOnServerAbstractTestBase {

    @BeforeAll
    public static void init() {
        System.setProperty(PROPERTY_NAME, Boolean.TRUE.toString());
    }

    @AfterAll
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
