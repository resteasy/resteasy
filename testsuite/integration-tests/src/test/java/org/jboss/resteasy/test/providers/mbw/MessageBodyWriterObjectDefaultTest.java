package org.jboss.resteasy.test.providers.mbw;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.providers.mbw.resource.MessageBodyWriterObjectMessage;
import org.jboss.resteasy.test.providers.mbw.resource.MessageBodyWriterObjectMessageBodyWriter;
import org.jboss.resteasy.test.providers.mbw.resource.MessageBodyWriterObjectResource;
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
 * @tpSubChapter Resteasy MessageBodyWriter<Object>
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.4
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MessageBodyWriterObjectDefaultTest {

    static Client client;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();

    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MessageBodyWriterObjectDefaultTest.class.getSimpleName());
        war.addClasses(MessageBodyWriterObjectMessage.class);
        return TestUtil.finishContainerPrepare(war, null, MessageBodyWriterObjectResource.class,
                MessageBodyWriterObjectMessageBodyWriter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MessageBodyWriterObjectDefaultTest.class.getSimpleName());
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Test
    public void testDefault() throws Exception {
        Invocation.Builder request = client.target(generateURL("/test")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ok", entity);
        Assertions.assertEquals("xx/yy", response.getHeaderString("Content-Type"));
        request = client.target(generateURL("/test/used")).request();
        response = request.get();
        Assertions.assertTrue(Boolean.parseBoolean(response.readEntity(String.class)));
    }

    @Test
    //RESTEASY-1730: Could not find MessageBodyWriter for response object of type: java.lang.Boolean of media type: application/octet-stream
    public void testGetBoolean() throws Exception {
        Invocation.Builder request = client.target(generateURL("/test/getbool")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("true", entity);
    }
}
