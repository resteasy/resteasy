package org.jboss.resteasy.embedded.test.providers.custom;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.embedded.test.EmbeddedServerTestBase;
import org.jboss.resteasy.embedded.test.providers.custom.resource.ReaderWriterResource;
import org.jboss.resteasy.embedded.test.providers.custom.resource.WriterNotBuiltinTestWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.embedded.test.TestPortProvider.generateURL;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Demonstrate MessageBodyWriter, MessageBodyReader
 * @tpSince RESTEasy 4.1.0
 */
public class WriterNotBuiltinTest extends EmbeddedServerTestBase {

    static ResteasyClient client;
    private static EmbeddedJaxrsServer server;

    @Before
    public void setup() throws Exception {
        server = getServer();

        ResteasyDeployment deployment = server.getDeployment();
        deployment.setRegisterBuiltin(false);
        deployment.getActualProviderClasses().add(WriterNotBuiltinTestWriter.class);
        deployment.getActualResourceClasses().add(ReaderWriterResource.class);
        server.setDeployment(deployment);

        server.start();
        server.deploy();

        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        server.stop();
    }

    /**
     * @tpTestDetails  TestReaderWriter has no type parameter,
     * so it comes after DefaultPlainText in the built-in ordering.
     * The fact that TestReaderWriter gets called verifies that
     * DefaultPlainText gets passed over.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void test1New() throws Exception {
        Response response = client.target(generateURL("/string")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("text/plain;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        Assert.assertEquals("Response contains wrong content", "hello world", response.readEntity(String.class));
        Assert.assertTrue("Wrong MessageBodyWriter was used", WriterNotBuiltinTestWriter.used);
    }
}
