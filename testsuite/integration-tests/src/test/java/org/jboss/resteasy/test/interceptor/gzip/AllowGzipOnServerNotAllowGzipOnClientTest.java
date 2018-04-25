package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.Test;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class AllowGzipOnServerNotAllowGzipOnClientTest extends GzipAbstractTest {

    /**
     * @tpTestDetails gzip is allowed on server by resteasy.allowGzip system property,
     *                gzip is allowed on client by manual import of gzip interceptors
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_without_providers_file")
    public void manuallyImportOnClient() throws Exception {
        testNormalClient(true, "true", true, true);
    }

    /**
     * @tpTestDetails gzip is allowed on server by resteasy.allowGzip system property
     *                gzip is disabled on client
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_with_providers_file")
    public void noGzipOnClient() throws Exception {
        testNormalClient(false, "true", false, false);
    }

}
