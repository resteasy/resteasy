package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;

import java.net.URL;

/**
 * @tpSubChapter Gzip
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1735
 * @tpSince RESTEasy 3.6
 */
public class NotAllowGzipOnServerNotAllowGzipOnClientTest extends NotAllowGzipOnServerAbstractTestBase {

   @ArquillianResource
   private URL deploymentBaseUrl;

   /**
     * @tpTestDetails gzip is disabled on server
     *                gzip is disabled on client
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITHOUT_PROVIDERS_FILE)
    public void noProvidersFileNoManualImportOnClient() throws Exception {
        testNormalClient(deploymentBaseUrl, false, "null", false, false);
    }

    /**
     * @tpTestDetails gzip is enabled on server by javax.ws.rs.ext.Providers file in deployment
     *                gzip is allowed on client by manual import of gzip interceptors
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITH_PROVIDERS_FILE)
    public void providersFileManualImportOnClient() throws Exception {
        testNormalClient(deploymentBaseUrl, true, "null", true, true);
    }


    /**
     * @tpTestDetails gzip is enabled on server by javax.ws.rs.ext.Providers file in deployment
     *                gzip is disabled on client
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITH_PROVIDERS_FILE)
    public void providersFileNoManualImportOnClient() throws Exception {
        testNormalClient(deploymentBaseUrl, false, "null", false, false);
    }


    /**
     * @tpTestDetails gzip is disabled on server
     *                gzip is allowed on client by manual import of gzip interceptors
     * @tpSince RESTEasy 3.6
     */
    @Test
    @OperateOnDeployment(WAR_WITHOUT_PROVIDERS_FILE)
    public void noProvidersFileManualImportOnClient() throws Exception {
        testNormalClient(deploymentBaseUrl, true, "null", true, false);
    }

}
