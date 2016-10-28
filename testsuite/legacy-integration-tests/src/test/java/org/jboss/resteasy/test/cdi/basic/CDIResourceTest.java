package org.jboss.resteasy.test.cdi.basic;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TimeoutUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1082
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CDIResourceTest {

    protected static final Logger logger = LogManager.getLogger(CDIResourceTest.class.getName());

    static final String fromStr;
    static final String toStr;

    static {
        fromStr = TestUtil.getResourcePath(CDIResourceTest.class, "RESTEASY-1082.war");
        toStr = new StringBuilder()
                .append(TestUtil.getJbossHome()).append(File.separator)
                .append("standalone").append(File.separator)
                .append("deployments").append(File.separator)
                .append("RESTEASY-1082.war").toString();
    }

    /**
     * @tpTestDetails Redeploy deployment with RESTEasy and CDI beans. Check errors.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCDIResourceFromServlet() throws Exception {
        Path from = FileSystems.getDefault().getPath(fromStr).toAbsolutePath();
        Path to = FileSystems.getDefault().getPath(toStr).toAbsolutePath();

        try {
            // Delete existing RESTEASY-1082.war, if any.
            try {
                Files.delete(to);
            } catch (Exception e) {
                // ok
            }

            // Deploy RESTEASY-1082.war
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Copied war to " + to);
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(PortProviderUtil.generateURL("/test", "RESTEASY-1082"));

            // Wait for RESTEASY-1082.war to be installed.
            HttpResponse response = client.execute(get);
            boolean succesInDeploy = false;
            for (int i = 0; i < 40; i++) {
                get.releaseConnection();
                response = client.execute(get);
                if (response.getStatusLine().getStatusCode() != HttpResponseCodes.SC_NOT_FOUND) {
                    succesInDeploy = true;
                    break;
                }
                Thread.sleep(TimeoutUtil.adjust(500));
            }
            Assert.assertTrue("Deployment was not deployed", succesInDeploy);
            logger.info("status: " + response.getStatusLine().getStatusCode());
            printResponse(response);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
            get.releaseConnection();

            // Redeploy RESTEASY-1082.war
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Replaced war");
            Thread.sleep(TimeoutUtil.adjust(5000));

            // Wait for RESTEASY-1082.war to be installed.
            response = client.execute(get);
            succesInDeploy = false;
            for (int i = 0; i < 40; i++) {
                get.releaseConnection();
                response = client.execute(get);
                if (response.getStatusLine().getStatusCode() != HttpResponseCodes.SC_NOT_FOUND) {
                    succesInDeploy = true;
                    break;
                }
                Thread.sleep(TimeoutUtil.adjust(500));
            }
            Assert.assertTrue("Deployment was not deployed", succesInDeploy);

            logger.info("status: " + response.getStatusLine().getStatusCode());
            printResponse(response);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
        } finally {
            Files.delete(to);
        }
    }

    protected void printResponse(HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = reader.readLine();
        logger.info("_____Response:_____");
        while (line != null) {
            logger.info(line);
            line = reader.readLine();
        }
        logger.info("___________________");
    }
}
