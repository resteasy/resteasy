package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.path.resource.EncodedPathResource;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests path encoding
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EncodedPathTest {
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
        WebArchive war = TestUtil.prepareArchive(EncodedPathTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, EncodedPathResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EncodedPathTest.class.getSimpleName());
    }

    private void _test(String path) {
        Builder builder = client.target(generateURL(path)).request();
        Response response = null;
        try {
            response = builder.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            response.close();
        }
    }

    @Test
    public void testEncoded() throws Exception {
        _test("/hello%20world");
        _test("/goodbye%7Bworld");
    }
}
