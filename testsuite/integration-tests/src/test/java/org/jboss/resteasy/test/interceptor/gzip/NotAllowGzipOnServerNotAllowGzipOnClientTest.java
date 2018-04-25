package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.Test;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class NotAllowGzipOnServerNotAllowGzipOnClientTest extends GzipAbstractTest {

    /**
     * @tpTestDetails gzip is disabled on server
     *                gzip is disabled on client
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_without_providers_file")
    public void noProvidersFileNoManualImportOnClient() throws Exception {
        testNormalClient(false, "null", false, false);
    }

    /**
     * @tpTestDetails gzip is enabled on server by javax.ws.rs.ext.Providers file in deployment
     *                gzip is allowed on client by manual import of gzip interceptors
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_with_providers_file")
    public void providersFileManualImportOnClient() throws Exception {
        testNormalClient(true, "null", true, true);
    }


    /**
     * @tpTestDetails gzip is enabled on server by javax.ws.rs.ext.Providers file in deployment
     *                gzip is disabled on client
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_with_providers_file")
    public void providersFileNoManualImportOnClient() throws Exception {
        testNormalClient(false, "null", false, false);
    }


    /**
     * @tpTestDetails gzip is disabled on server
     *                gzip is allowed on client by manual import of gzip interceptors
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment("war_without_providers_file")
    public void noProvidersFileManualImportOnClient() throws Exception {
        testNormalClient(true, "null", true, false);
    }

}
