package org.jboss.resteasy.test.response;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.response.resource.SimpleResource;
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
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails HEAD requests always return non-null Content-Length
 * @tpInfo RESTEASY-1365
 * @tpSince RESTEasy 3.0.19
 * @author Ivo Studensky
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HeadContentLengthTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(HeadContentLengthTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SimpleResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HeadContentLengthTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails HEAD requests always return non-null Content-Length
     * @tpSince RESTEasy 3.0.19
     */
    @Test
    public void testHeadContentLength() {
        Builder builder = client.target(generateURL("/simpleresource")).request();
        builder.accept(MediaType.TEXT_PLAIN_TYPE);

        Response getResponse = builder.get();
        String responseBody = getResponse.readEntity(String.class);
        Assertions.assertEquals("hello", responseBody, "The response body doesn't match the expected");
        int getResponseLength = getResponse.getLength();
        Assertions.assertEquals(5, getResponseLength, "The response length doesn't match the expected");

        Response headResponse = builder.head();
        int headResponseLength = headResponse.getLength();
        Assertions.assertEquals(getResponseLength, headResponseLength,
                "The response length from GET and HEAD request doesn't match");
    }

}
