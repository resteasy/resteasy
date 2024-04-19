package org.jboss.resteasy.test.providers.datasource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.util.PropertyPermission;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.datasource.resource.CleanFilesDataSourceProviderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter DataSource provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CleanFilesDataSourceProviderTest {

    protected static final Logger logger = Logger.getLogger(CleanFilesDataSourceProviderTest.class.getName());
    static ResteasyClient client;
    static String serverTmpDir;

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CleanFilesDataSourceProviderTest.class.getSimpleName());
        // DataSource provider creates tmp file in the filesystem
        war.addAsManifestResource(
                DeploymentDescriptors.createPermissionsXmlAsset(DeploymentDescriptors.createTempDirPermission("read"),
                        new PropertyPermission("java.io.tmpdir", "read")),
                "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, CleanFilesDataSourceProviderResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
        serverTmpDir = getTmpDirectory();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CleanFilesDataSourceProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read the same stream and then checks whether number of temporary
     *                files is same as before request. The manipulation with DataSourceProvider happens on the server, no data
     *                are send
     *                back and forth
     * @tpInfo RESTEASY-1182
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDataSourceProviderInputStreamOnce() throws Exception {
        // count temporary files before
        int countBefore = countFiles(serverTmpDir);
        logger.info("Count of Resteasy temporary files in " + serverTmpDir + " before request: " + countBefore);

        // http request
        HttpClient httpClient = HttpClients.custom().build();
        HttpPost httpPost = new HttpPost(generateURL("/once"));
        httpPost.setHeader("Content-type", "application/octet-stream");
        httpPost.setEntity(new ByteArrayEntity(new byte[5 * 1024]));
        HttpResponse response = httpClient.execute(httpPost);
        // check http request results
        int postStatus = response.getStatusLine().getStatusCode();
        String postResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        Assertions.assertEquals(HttpStatus.SC_OK, postStatus, "Status of client request is not correct.");
        Assertions.assertEquals(CleanFilesDataSourceProviderResource.clientResponse,
                postResponse, "Client get wrong response.");

        // count temporary files after
        int countAfter = countFiles(serverTmpDir);
        logger.info("Count of Resteasy temporary files in " + serverTmpDir + " after request: " + countAfter);

        // Compare
        Assertions.assertEquals(countBefore, countAfter, "Client request remove or add some temporary files.");
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read the same stream twice and then checks whether number of
     *                temporary
     *                files is same as before request. The manipulation with DataSourceProvider happens on the server, no data
     *                are send
     *                back and forth
     * @tpInfo RESTEASY-1182
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDataSourceProviderInputStreamTwice() throws Exception {
        // count temporary files before
        int countBefore = countFiles(serverTmpDir);
        logger.info("Count of Resteasy temporary files in " + serverTmpDir + " before request: " + countBefore);

        // http request
        HttpClient httpClient = HttpClients.custom().build();
        HttpPost httpPost = new HttpPost(generateURL("/twice"));
        httpPost.setHeader("Content-type", "application/octet-stream");
        httpPost.setEntity(new ByteArrayEntity(new byte[5 * 1024]));
        HttpResponse response = httpClient.execute(httpPost);
        // check http request results
        int postStatus = response.getStatusLine().getStatusCode();
        String postResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        Assertions.assertEquals(HttpStatus.SC_OK, postStatus,
                TestUtil.getErrorMessageForKnownIssue("JBEAP-1904", "Status of client request is not correct."));
        Assertions.assertEquals(CleanFilesDataSourceProviderResource.clientResponse,
                postResponse, "Client get wrong response.");

        // count temporary files after
        int countAfter = countFiles(serverTmpDir);
        logger.info("Count of Resteasy temporary files in " + serverTmpDir + " after request: " + countAfter);

        // Compare
        Assertions.assertEquals(countBefore, countAfter, "Client request remove or add some temporary files.");
    }

    /**
     * @tpTestDetails Tests that DataSourceProvider removes temporary file it creates in the case when input stream is not read.
     * @tpInfo RESTEASY-1670
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testDataSourceProviderInputStreamNotRead() throws Exception {
        // count temporary files before
        int countBefore = countFiles(serverTmpDir);
        logger.info("Count of Resteasy temporary files in " + serverTmpDir + " before request: " + countBefore);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
        for (int i = 0; i < 5000; i++) {
            baos.write(i);
        }
        Response response = client.target(generateURL("/never")).request()
                .post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        // count temporary files after
        int countAfter = countFiles(serverTmpDir);
        logger.info("Count of Resteasy temporary files in " + serverTmpDir + " after request: " + countAfter);

        // Compare
        Assertions.assertEquals(countBefore, countAfter, "Client request removed or added some temporary files.");
    }

    private static String getTmpDirectory() {
        Response response = client.target(generateURL("/tmpdirpath")).request().get();
        return response.readEntity(String.class);
    }

    private int countFiles(String dir) {
        File tmpdir = new File(dir);
        Assertions.assertTrue(tmpdir.isDirectory(), dir + " does not exists");
        logger.info("Tmp directory = " + tmpdir);

        // Get count of Resteasy temporary files
        String[] tmpfiles = tmpdir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("resteasy-provider-datasource");
            }
        });
        return tmpfiles.length;
    }
}
