package org.jboss.resteasy.test.providers.datasource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.datasource.resource.BigSmallDataSourceResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter DataSource provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BigSmallDataSourceTest {

    static ResteasyClient client;
    static final String testFilePath;

    static {
        testFilePath = TestUtil.getResourcePath(BigSmallDataSourceTest.class, "test.jpg");
    }

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(BigSmallDataSourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, BigSmallDataSourceResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, BigSmallDataSourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to get content type of the file attached to the request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostDataSource() throws Exception {
        File file = new File(testFilePath);
        Assertions.assertTrue(file.exists(), "File " + testFilePath + " doesn't exists");
        WebTarget target = client.target(generateURL("/jaf"));
        Response response = target.request().post(Entity.entity(file, "image/jpeg"));
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("image/jpeg",
                response.readEntity(String.class),
                "Unexpected content type returned from the server");
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read and write bigger file
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEchoDataSourceBigData() throws Exception {
        WebTarget target = client.target(generateURL("/jaf/echo"));
        File file = new File(testFilePath);
        Assertions.assertTrue(file.exists(), "File " + testFilePath + " doesn't exists");
        Response response = target.request().post(Entity.entity(file, "image/jpeg"));
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        InputStream ris = null;
        InputStream fis = null;
        try {
            ris = response.readEntity(InputStream.class);
            fis = new FileInputStream(file);
            int fi;
            int ri;
            do {
                fi = fis.read();
                ri = ris.read();
                if (fi != ri) {
                    Assertions.fail("The sent and received stream is not identical.");
                }
            } while (fi != -1);
        } finally {
            if (ris != null) {
                ris.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read and write small stream
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEchoDataSourceSmallData() throws Exception {
        WebTarget target = client.target(generateURL("/jaf/echo"));
        byte[] input = "Hello World!".getBytes(StandardCharsets.UTF_8);
        Response response = target.request().post(Entity.entity(input, MediaType.APPLICATION_OCTET_STREAM));
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        InputStream ris = null;
        InputStream bis = null;
        try {
            ris = response.readEntity(InputStream.class);
            bis = new ByteArrayInputStream(input);
            int fi;
            int ri;
            do {
                fi = bis.read();
                ri = ris.read();
                if (fi != ri) {
                    Assertions.fail("The sent and recived stream is not identical.");
                }
            } while (fi != -1);
        } finally {
            if (ris != null) {
                ris.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to return InputStream for given value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDataSource() throws Exception {
        String value = "foo";
        WebTarget target = client.target(generateURL("/jaf") + "/" + value);
        Response response = target.request().get();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals(value, response.readEntity(String.class), "The unexpected value returned from InputStream");
    }
}
