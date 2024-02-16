package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.HttpHeadersResource;
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
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Spec requires that HEAD and OPTIONS are handled in a default manner
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HttpHeadersTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(HttpHeadersTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, HttpHeadersResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HttpHeadersTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client invokes GET request on a sub resource at /HeadersTest/sub2
     *                with Accept MediaType and Content-Type Headers set;
     *                Verify that HttpHeaders got the property set by the request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void RequestHeadersTest() throws Exception {
        String errorMessage = "Wrong content of response";
        Response response = client.target(generateURL("/HeadersTest/headers")).request()
                .header("Accept", "text/plain, text/html, text/html;level=1, */*")
                .header("Content-Type", "application/xml;charset=utf8")
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String content = response.readEntity(String.class);

        Assertions.assertTrue(-1 < content.indexOf("Accept:"), errorMessage);
        Assertions.assertTrue(-1 < content.indexOf("Content-Type:"), errorMessage);
        Assertions.assertTrue(-1 < content.indexOf("application/xml"), errorMessage);
        Assertions.assertTrue(-1 < content.indexOf("charset=utf8"), errorMessage);
        Assertions.assertTrue(-1 < content.indexOf("text/html"), errorMessage);
        Assertions.assertTrue(-1 < content.indexOf("*/*"), errorMessage);

        response.close();
    }

}
