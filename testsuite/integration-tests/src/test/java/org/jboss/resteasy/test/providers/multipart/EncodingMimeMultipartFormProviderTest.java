package org.jboss.resteasy.test.providers.multipart;

import java.io.File;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.multipart.resource.EncodingMimeMultipartFormProviderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EncodingMimeMultipartFormProviderTest {

    private static final String TEST_URI = generateURL("/encoding-mime");
    // file with non ASCII character
    private static final String testFilePath = TestUtil.getResourcePath(EncodingMimeMultipartFormProviderTest.class,
            "EncodingMimeMultipartFormProviderTestData.txt");

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EncodingMimeMultipartFormProviderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, EncodingMimeMultipartFormProviderResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EncodingMimeMultipartFormProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test of filename encoding
     * @tpSince RESTEasy 3.6.0
     */
    @Test
    public void testPostFormFile() throws Exception {
        // prepare file
        File file = new File(testFilePath);
        Assertions.assertTrue(file.exists(), "File " + testFilePath + " doesn't exists");

        MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
        mpfdo.addFormData("file_upload", file, MediaType.APPLICATION_OCTET_STREAM_TYPE,
                EncodingMimeMultipartFormProviderResource.FILENAME_NON_ASCII, true);

        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(TEST_URI + "/file").request()
                .post(Entity.entity(mpfdo, MediaType.MULTIPART_FORM_DATA_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        client.close();
    }
}
