package org.jboss.resteasy.test.providers.multipart;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.multipart.resource.EmptyInputStreamMultipartProviderMyBean;
import org.jboss.resteasy.test.providers.multipart.resource.EmptyInputStreamMultipartProviderResource;
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
 * @tpTestCaseDetails Regression test for RESTEASY-204.
 *                    POJO with empty InputStream field returned as "mutlipart/form-data" produces no headers in multipart
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EmptyInputStreamMultipartProviderTest {

    protected final Logger logger = Logger.getLogger(EmptyInputStreamMultipartProviderTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EmptyInputStreamMultipartProviderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, EmptyInputStreamMultipartProviderResource.class,
                EmptyInputStreamMultipartProviderMyBean.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EmptyInputStreamMultipartProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Resource returning POJO with empty InputStream field, the response is checked to contain the header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/rest/zba"));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String string = response.readEntity(String.class);
        logger.info(string);
        Assertions.assertTrue(string.indexOf("Content-Length") > -1, "The response doesn't contain the expected header");
        client.close();
    }

}
