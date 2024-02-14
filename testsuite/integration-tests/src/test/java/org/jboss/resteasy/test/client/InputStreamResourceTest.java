package org.jboss.resteasy.test.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.test.client.resource.InputStreamResourceClient;
import org.jboss.resteasy.test.client.resource.InputStreamResourceService;
import org.jboss.resteasy.util.ReadFromStream;
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
 * @tpTestCaseDetails Read and write InputStreams
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InputStreamResourceTest extends ClientTestBase {

    static Client resteasyClient;

    @BeforeAll
    public static void setup() {
        resteasyClient = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        resteasyClient.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InputStreamResourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, InputStreamResourceService.class);
    }

    /**
     * @tpTestDetails Read Strings as either Strings or InputStreams
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testClientResponse() throws Exception {
        InputStreamResourceClient client = ProxyBuilder
                .builder(InputStreamResourceClient.class, resteasyClient.target(generateURL(""))).build();
        Assertions.assertEquals("hello", client.getAsString());
        Response is = client.getAsInputStream();
        Assertions.assertEquals("hello", new String(ReadFromStream.readFromStream(1024, is.readEntity(InputStream.class))));
        is.close();
        client.postString("new value");
        Assertions.assertEquals("new value", client.getAsString());
        client.postInputStream(new ByteArrayInputStream("new value 2".getBytes()));
        Assertions.assertEquals("new value 2", client.getAsString());
        client.postInputStream(new ByteArrayInputStream("".getBytes()));
        Assertions.assertEquals("", client.getAsString());
    }
}
