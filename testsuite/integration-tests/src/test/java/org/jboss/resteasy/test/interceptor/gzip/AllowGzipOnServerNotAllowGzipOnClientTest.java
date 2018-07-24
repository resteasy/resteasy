package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.Test;

import java.net.URL;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class AllowGzipOnServerNotAllowGzipOnClientTest extends AllowGzipOnServerAbstractTestBase {

    /**
     * @tpTestDetails gzip is allowed on server by resteasy.allowGzip system property,
     *                gzip is allowed on client by manual import of gzip interceptors
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITHOUT_PROVIDERS_FILE)
    public void manuallyImportOnClient() throws Exception {
        testNormalClient(new URL(gzipServerBaseUrl + "/" + WAR_WITHOUT_PROVIDERS_FILE), true, "true", true, true);
    }

    /**
     * @tpTestDetails gzip is allowed on server by resteasy.allowGzip system property
     *                gzip is disabled on client
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITH_PROVIDERS_FILE)
    public void noGzipOnClient() throws Exception {
        testNormalClient(new URL(gzipServerBaseUrl + "/" + WAR_WITH_PROVIDERS_FILE), false, "true", false, false);
    }

}
