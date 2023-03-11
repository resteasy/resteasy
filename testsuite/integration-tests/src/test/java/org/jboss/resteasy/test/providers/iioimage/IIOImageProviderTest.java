package org.jboss.resteasy.test.providers.iioimage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.iioimage.resource.ImageResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter IIOImage provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic test for IIOImage provider. Old issue: https://issues.jboss.org/browse/RESTEASY-862
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class IIOImageProviderTest {
    static ResteasyClient client;
    //two different versions of the same png image, compressed using JDK8 and JDK11, so that we can perform byte comparisons in testPostPNGImage()
    static final String testPngResource1 = "test1.png";
    static final String testPngResource2 = "test2.png";
    static final String testWdpResource = "test.wdp";

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private static final String TEST_URI = generateURL("/image");

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(IIOImageProviderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ImageResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, IIOImageProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test a post of a PNG image whose response should be a PNG version of the
     *                same photo.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostPNGImage() throws Exception {
        final String testPngResource = System.getProperty("java.version").startsWith("1.") ? testPngResource1
                : testPngResource2;
        File file = new File(TestUtil.getResourcePath(IIOImageProviderTest.class, testPngResource));
        Assert.assertTrue(file.exists());
        Response response = client.target(TEST_URI).request().post(Entity.entity(file, "image/png"));
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String contentType = response.getHeaderString("content-type");
        Assert.assertEquals("Wrong content type of response", "image/png", contentType);

        BufferedInputStream in = new BufferedInputStream(response.readEntity(InputStream.class));
        ByteArrayOutputStream fromServer = new ByteArrayOutputStream();
        writeTo(in, fromServer);
        response.close();
        File savedPng = new File(TestUtil.getResourcePath(IIOImageProviderTest.class, testPngResource));
        FileInputStream fis = new FileInputStream(savedPng);
        ByteArrayOutputStream fromTestData = new ByteArrayOutputStream();
        writeTo(fis, fromTestData);
        // ImageResource could change image slightly, so next assert could fail, because same picture could have been saved different
        Assert.assertTrue("ImageResource could change image slightly or ImageResource is wrong",
                Arrays.equals(fromServer.toByteArray(), fromTestData.toByteArray()));
    }

    /**
     * @tpTestDetails Tests a image format that is not directly supported by Image IO. In this
     *                case, an HD Photo image is posted to the Resource which should return a
     *                406 - Not Acceptable response. The response body should include a list of
     *                variants that are supported by the application.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostUnsupportedImage() throws Exception {
        File file = new File(TestUtil.getResourcePath(IIOImageProviderTest.class, testWdpResource));
        Assert.assertTrue(file.exists());
        Response response = client.target(TEST_URI).request().post(Entity.entity(file, "image/vnd.ms-photo"));
        Assert.assertEquals("Unsupported image is accepted by server", HttpServletResponse.SC_NOT_ACCEPTABLE,
                response.getStatus());
        response.close();
    }

    public void writeTo(final InputStream in, final OutputStream out) throws IOException {
        int read;
        final byte[] buf = new byte[2048];
        while ((read = in.read(buf)) != -1) {
            out.write(buf, 0, read);
        }
    }
}
