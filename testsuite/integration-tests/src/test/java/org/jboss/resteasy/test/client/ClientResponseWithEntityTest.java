package org.jboss.resteasy.test.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.test.client.resource.ClientResponseWithEntityResponseFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class ClientResponseWithEntityTest {

    @XmlRootElement
    public static class Message {
        private String message;

        public Message() {
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Path("echo")
    @Produces(MediaType.APPLICATION_XML)
    public static class EchoResource {

        @GET
        public Response echo(@QueryParam("msg") String msg) {
            Message message = new Message();
            message.setMessage(String.valueOf(msg));
            return Response.ok(message).build();
        }

    }

    private static Client client;
    private static final String DEP = "ClientResponseWithEntityTest";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DEP);
        war.addClass(Message.class);
        war.addClass(EchoResource.class);
        return TestUtil.finishContainerPrepare(war, null, EchoResource.class);
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
    }

    private static String generateURL() {
        return PortProviderUtil.generateBaseUrl(DEP);
    }

    @Test
    public void Should_ReturnEntity_When_NoNull() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);
        try (ClientResponse response = (ClientResponse) request.get()) {
            Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
            Assert.assertTrue(response.hasEntity());
            Assert.assertNotNull(response.getEntity());
            Assert.assertNotNull(response.getEntityClass());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void Should_ThrowIllegalStateException_When_EntityIsConsumed() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);
        try (ClientResponse response = (ClientResponse) request.get()) {
            Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
            Assert.assertTrue(response.hasEntity());
            InputStream entityStream = (InputStream) response.getEntity();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int wasRead = 0;
            do {
                wasRead = entityStream.read(buffer);
                if (wasRead > 0) {
                    baos.write(buffer, 0, wasRead);
                }
            } while (wasRead > -1);
            response.getEntity();
        }
    }

    /**
     *
     * According to {@link Response#getEntity()} java doc if the entity was previously fully consumed as an {@link InputStream
     * input stream}
     * an {@link IllegalStateException} MUST be thrown.
     *
     * @throws IOException
     */
    @Test
    public void getEntity_Should_ThrowIllegalStateException_When_EntityIsInputStream_And_IsFullyConsumed() throws IOException {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);

        //entity retrieved as an input stream using response.getEntity() and then fully consumed => response.getEntity() MUST throw an IllegalStateException
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            //Fully consumed the original response stream
            while (((InputStream) response.getEntity()).read() != -1) {
            }
            try {
                response.getEntity();
                Assert.fail("An IllegalStateException was expected.");
            } catch (Exception e) {
                Assert.assertTrue(IllegalStateException.class.isInstance(e));
                // Following is to be sure that previous IllegalStateException is not because of closed response
                try {
                    Assert.assertTrue(response.hasEntity());
                } catch (IllegalStateException e2) {
                    Assert.fail("The response was not supposed to be closed");
                }
            }
        }

        //entity retrieved as an input stream using response.readEntity(InputStream.class) and then fully consumed => response.getEntity() MUST throw an IllegalStateException
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            //Fully consumed the original response stream
            while (response.readEntity(InputStream.class).read() != -1) {
            }
            try {
                response.getEntity();
                Assert.fail("An IllegalStateException was expected.");
            } catch (Exception e) {
                Assert.assertTrue(IllegalStateException.class.isInstance(e));
                // Following is to be sure that previous IllegalStateException is not because of closed response
                try {
                    Assert.assertTrue(response.hasEntity());
                } catch (IllegalStateException e2) {
                    Assert.fail("The response was not supposed to be closed");
                }
            }
        }
    }

    @Test
    public void getEntity_Should_ReturnEntity_When_EntityIsInputStream_And_IsNotFullyConsumed() throws IOException {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);

        //entity retrieved as an input stream using response.getEntity() and then partially consumed => response.getEntity() MUST return input stream
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            InputStream entityStream = (InputStream) response.getEntity();
            //Let consume a part of the entity stream
            Assert.assertTrue(-1 != ((InputStream) response.getEntity()).read());
            Assert.assertTrue(InputStream.class.isInstance(response.getEntity()));
        }

        //entity retrieved as an input stream using response.readEntity(InputStream.class) and then partially consumed => response.getEntity() MUST return input stream
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            //Let consume a part of the entity stream
            Assert.assertTrue(-1 != response.readEntity(InputStream.class).read());
            Assert.assertTrue(InputStream.class.isInstance(response.getEntity()));
        }
    }

    /**
     * According to {@link Response#bufferEntity()} java doc, if the response entity instance is not backed by an unconsumed
     * input stream, the method invocation is
     * ignored and the method MUST returns false.
     *
     * @throws IOException
     */
    @Test
    public void bufferEntity_Should_ReturnFalse_When_EntityInputStreamIsNotUnconsumed() throws IOException {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);

        //entity retrieved as an input stream using response.getEntity() and then consumed => response.bufferEntity() MUST return false
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            Assert.assertTrue(-1 != ((InputStream) response.getEntity()).read());
            Assert.assertFalse(response.bufferEntity());
        }

        //entity retrieved as an input stream using response.readEntity(InputStream.class) and then consumed => response.bufferEntity() MUST return false
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            Assert.assertTrue(-1 != response.readEntity(InputStream.class).read());
            Assert.assertFalse(response.bufferEntity());
        }
    }

    @Test
    public void bufferEntity_Should_ReturnTrue_When_EntityInputStreamIsUnconsumed() {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);

        //entity retrieved as an input stream using response.getEntity() and not consumed => response.bufferEntity() MUST return true
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            Assert.assertTrue(InputStream.class.isInstance(response.getEntity()));
            Assert.assertTrue(response.bufferEntity());
        }

        //entity retrieved as an input stream using response.readEntity(InputStream.class) and not consumed => response.bufferEntity() MUST return true
        try (Response response = request.get();) {
            Assert.assertTrue(response.hasEntity());
            Assert.assertTrue(InputStream.class.isInstance(response.readEntity(InputStream.class)));
            Assert.assertTrue(response.bufferEntity());
        }
    }

    @Test
    public void bufferEntity_Should_ReturnTrue_When_InputStream_Is_Replaced() {
        Client client = ClientBuilder.newClient();
        client.register(ClientResponseWithEntityResponseFilter.class);
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.APPLICATION_XML_TYPE);

        //entity retrieved as an input stream using response.getEntity() and not consumed => response.bufferEntity() MUST return true
        try (Response response = request.get();) {
            Assert.assertTrue(ClientResponseWithEntityResponseFilter.called());
            Assert.assertTrue(response.bufferEntity());
        }
        client.close();
    }
}
