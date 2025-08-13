package org.jboss.resteasy.test.providers.iioimage;

import static org.jboss.resteasy.test.providers.iioimage.resource.ImageResource.CONTENT_TYPE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.iioimage.resource.ImageResource;
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
 * @tpSubChapter IIOImage provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic test for IIOImage provider. Old issue: https://issues.jboss.org/browse/RESTEASY-862
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class IIOImageProviderTest {
    static ResteasyClient client;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        File localImage = createNewPng(
                ImageIO.read(new File(TestUtil.getResourcePath(IIOImageProviderTest.class, "test.png"))));
        Assertions.assertTrue(localImage.exists());

        Response response = client.target(TEST_URI).request().post(Entity.entity(localImage, CONTENT_TYPE));
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals(CONTENT_TYPE, response.getHeaderString("content-type"), "Wrong content type of response");

        ByteArrayOutputStream fromServer = new ByteArrayOutputStream();
        writeTo(response.readEntity(InputStream.class), fromServer);
        response.close();

        ByteArrayOutputStream localData = new ByteArrayOutputStream();
        writeTo(new FileInputStream(localImage), localData);

        // ImageResource could change image slightly, so next assert could fail, because same picture could have been saved different
        Assertions.assertTrue(Arrays.equals(fromServer.toByteArray(), localData.toByteArray()),
                "ImageResource could change image slightly or ImageResource is wrong");
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
        final String testWdpResource = "test.wdp";

        File file = new File(TestUtil.getResourcePath(IIOImageProviderTest.class, testWdpResource));
        Assertions.assertTrue(file.exists());
        Response response = client.target(TEST_URI).request().post(Entity.entity(file, "image/vnd.ms-photo"));
        Assertions.assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE,
                response.getStatus(), "Unsupported image is accepted by server");
        response.close();
    }

    private void writeTo(final InputStream in, final OutputStream out) throws IOException {
        int read;
        final byte[] buf = new byte[2048];
        while ((read = in.read(buf)) != -1) {
            out.write(buf, 0, read);
        }
    }

    /*
     * Under certain JDKs (e.g., OpenJDK and Oracle) the compression library can cause a false negative in the test due
     * to a slight change in the bits of the image, even though the two compared images are visually identical. This method
     * reads in the source image and creates a new PNG, implicitly using the underlying libraries of the JDK that is running
     * both the test and the server. This should remove any differences caused by varying compression libraries and verify
     * that Resteasy is handling the IIOImage payloads correctly.
     */
    private File createNewPng(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

        try (ImageOutputStream output = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(output);
            IIOImage iioImage = new IIOImage(image, null, null);
            ImageWriteParam param = writer.getDefaultWriteParam();
            writer.write(null, iioImage, param);
            File imageFile = File.createTempFile("test", ".png");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                baos.writeTo(fos);
            }
            baos.close();

            return imageFile;
        } finally {
            writer.dispose();
        }
    }
}
