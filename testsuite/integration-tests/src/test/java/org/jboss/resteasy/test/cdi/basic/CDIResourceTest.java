package org.jboss.resteasy.test.cdi.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.ContainerConstants;
import org.jboss.resteasy.test.cdi.basic.resource.resteasy1082.FooResource;
import org.jboss.resteasy.test.cdi.basic.resource.resteasy1082.TestApplication;
import org.jboss.resteasy.test.cdi.basic.resource.resteasy1082.TestServlet;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TimeoutUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1082
 * @tpSince RESTEasy 3.0.16
 *
 *          Jul 27, 2018 Test rewritten to generated the needed archive and write it to disk.
 */

@ExtendWith(ArquillianExtension.class)
@RunAsClient
@Tag("NotForBootableJar")
public class CDIResourceTest {

    protected static final Logger logger = Logger.getLogger(CDIResourceTest.class.getName());

    private static final String WAR_NAME = "RESTEASY-1082.war";
    static final String toStr;
    static final File exportFile;

    static {
        toStr = new StringBuilder()
                .append(TestUtil.getStandaloneDir(ContainerConstants.DEFAULT_CONTAINER_QUALIFIER)).append(File.separator)
                .append("deployments").append(File.separator)
                .append(WAR_NAME).toString();
        exportFile = new File(FileSystems.getDefault().getPath("target").toFile(), WAR_NAME);
    }

    @BeforeEach
    public void createArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, WAR_NAME);
        war.addClasses(FooResource.class,
                TestApplication.class,
                TestServlet.class);
        war.addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        war.addAsWebInfResource(CDIResourceTest.class.getPackage(),
                "web-resteasy1082.xml", "web.xml");
        //write file to disk
        war.as(ZipExporter.class).exportTo(exportFile, true);
    }

    /**
     * @tpTestDetails Redeploy deployment with RESTEasy and CDI beans. Check errors.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCDIResourceFromServlet() throws Exception {
        Path from = FileSystems.getDefault().getPath(exportFile.getAbsolutePath());
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
            Assertions.assertTrue(succesInDeploy, "Deployment was not deployed");
            logger.info("status: " + response.getStatusLine().getStatusCode());
            printResponse(response);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
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
            Assertions.assertTrue(succesInDeploy, "Deployment was not deployed");

            logger.info("status: " + response.getStatusLine().getStatusCode());
            printResponse(response);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
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
