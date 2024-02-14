package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.basic.resource.FileExtensionMappingApplication;
import org.jboss.resteasy.test.core.basic.resource.FileExtensionMappingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter MediaType
 * @tpChapter Integration tests
 * @tpTestCaseDetails Mapping file extensions to media types
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FileExtensionMappingTest {
    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(FileExtensionMappingTest.class.getSimpleName());
        war.addClass(FileExtensionMappingApplication.class);
        war.addAsWebInfResource(FileExtensionMappingTest.class.getPackage(), "FileExtensionMapping.xml", "web.xml");
        Archive<?> archive = TestUtil.finishContainerPrepare(war, null, FileExtensionMappingResource.class);
        return archive;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FileExtensionMappingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Map suffix .txt to Accept: text/plain
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testFileExtensionMappingPlain() throws Exception {
        Response response = client.target(generateURL("/test.txt")).queryParam("query", "whosOnFirst").request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("plain: whosOnFirst", entity);
    }

    /**
     * @tpTestDetails Map suffix .html to Accept: text/html
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testFileExtensionMappingHtml() throws Exception {
        Response response = client.target(generateURL("/test.html")).queryParam("query", "whosOnFirst").request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("html: whosOnFirst", entity);
    }
}
