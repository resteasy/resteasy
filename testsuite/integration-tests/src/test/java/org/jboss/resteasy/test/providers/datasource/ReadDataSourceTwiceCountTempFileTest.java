package org.jboss.resteasy.test.providers.datasource;

import java.io.ByteArrayOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.PropertyPermission;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.datasource.resource.ReadDataSourceTwiceCountTempFileResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
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
public class ReadDataSourceTwiceCountTempFileTest {

    protected static final Logger logger = Logger.getLogger(ReadDataSourceTwiceCountTempFileResource.class.getName());

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ReadDataSourceTwiceCountTempFileResource.class.getSimpleName());
        // DataSource provider creates tmp file in the filesystem
        war.addAsManifestResource(
                DeploymentDescriptors.createPermissionsXmlAsset(DeploymentDescriptors.createTempDirPermission("read"),
                        new PropertyPermission("java.io.tmpdir", "read")),
                "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ReadDataSourceTwiceCountTempFileResource.class);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ReadDataSourceTwiceCountTempFileResource.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read the same stream twice, consuming content of whole stream
     *                before reading the second and verifies that no temporary file left after stream is closed
     * @tpInfo RESTEASY-1182
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFileNotFound() throws Exception {
        WebTarget target = client.target(generateURL("/post"));

        //Count files initially
        int beginning = countTempFiles();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
        for (int i = 0; i < 5000; i++) {
            baos.write(i);
        }
        Response response = target.request().post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        logger.info("The status of the response is " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                TestUtil.getErrorMessageForKnownIssue("JBEAP-2847"));
        int counter = response.readEntity(int.class);
        int updated = countTempFiles();
        logger.info("counter from beginning (before request): " + beginning);
        logger.info("counter from server: " + counter);
        logger.info("counter updated: " + countTempFiles());
        Assertions.assertTrue(counter > updated,
                "The number of temporary files for datasource before and after request is not the same");
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read the same stream twice, consuming content of whole stream
     *                before reading the second and verifies that no temporary file left after stream is closed. The request is
     *                send multiple
     *                times and then number of files is verified
     * @tpInfo RESTEASY-1182
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFileNotFoundMultipleRequests() throws Exception {
        WebTarget target = client.target(generateURL("/post"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
        for (int i = 0; i < 5000; i++) {
            baos.write(i);
        }
        Response response = target.request().post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        logger.info("The status of the response is " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                TestUtil.getErrorMessageForKnownIssue("JBEAP-2847"));
        int counter = response.readEntity(int.class);

        response = target.request().post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        response.close();

        response = target.request().post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        response.close();

        response = target.request().post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        response.close();

        int updated = countTempFiles();
        logger.info("counter from server: " + counter);
        logger.info("counter updated: " + countTempFiles());
        Assertions.assertTrue(counter > updated,
                "The number of temporary files for datasource before and after request is not the same");
    }

    static int countTempFiles() throws Exception {
        String tmpdir = System.getProperty("java.io.tmpdir");
        Path dir = Paths.get(tmpdir);
        final AtomicInteger counter = new AtomicInteger(0);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "resteasy-provider-datasource*")) {
            stream.forEach(path -> counter.incrementAndGet());
        }
        return counter.intValue();
    }

    @AfterAll
    public static void afterclass() throws Exception {
        String tmpdir = System.getProperty("java.io.tmpdir");
        Path dir = Paths.get(tmpdir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "resteasy-provider-datasource*")) {
            stream.forEach(path -> logger.info(path.toString()));
        }
    }
}
