package org.jboss.resteasy.test.response;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.response.resource.WriterMatchingBoolWriter;
import org.jboss.resteasy.test.response.resource.WriterMatchingResource;
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
 * @tpSubChapter Writers
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class WriterMatchingTest {

    static Client client;

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WriterMatchingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(WriterMatchingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, WriterMatchingResource.class, WriterMatchingBoolWriter.class);
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails Check correct sort of writers. RESTEasy should check correct writer.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatch() {
        // writers sorted by type, mediatype, and then by app over builtin
        Response response = client.target(generateURL("/bool")).request("text/plain").get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String data = response.readEntity(String.class);
        response.close();
        Assertions.assertEquals(data, "true", "RESTEasy returns wrong data");
    }

}
