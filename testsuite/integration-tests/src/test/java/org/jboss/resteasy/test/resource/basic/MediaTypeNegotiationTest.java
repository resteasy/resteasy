package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MediaTypeNegotiationTest {

    @XmlRootElement
    public static class Message {
        private int status;
        private String message;

        public Message() {
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Path("echo")
    public static class EchoResource {
        @Produces({ "*/xml" })
        @GET
        public Response echo(@QueryParam("msg") String msg) {
            Message message = new Message();
            message.setStatus(Status.OK.getStatusCode());
            message.setMessage(String.valueOf(msg));
            return Response.ok(message).build();
        }

        @Produces({ "foo/bar" })
        @GET
        @Path("missingMBW")
        public Response echoMissingMBW(@QueryParam("msg") String msg) {
            Message message = new Message();
            message.setStatus(Status.OK.getStatusCode());
            message.setMessage(String.valueOf(msg));
            return Response.ok(message).build();
        }
    }

    public static class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
        @Override
        public Response toResponse(NotFoundException notFoundException) {
            Message message = new Message();
            message.setStatus(Status.NOT_FOUND.getStatusCode());
            message.setMessage(Status.NOT_FOUND.getReasonPhrase());
            return Response.status(Status.NOT_FOUND).entity(message).build();
        }
    }

    public static class NoMessageBodyWriterFoundFailureExceptionMapper
            implements ExceptionMapper<NoMessageBodyWriterFoundFailure> {
        @Override
        public Response toResponse(NoMessageBodyWriterFoundFailure noMessageBodyWriterFoundFailure) {
            Message message = new Message();
            message.setStatus(Status.INTERNAL_SERVER_ERROR.getStatusCode());
            message.setMessage(noMessageBodyWriterFoundFailure.getClass().getName());
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML_TYPE).entity(message).build();
        }
    }

    private static Client client;
    private static final String DEP = "MediaTypeNegotiationTest";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DEP);
        war.addClass(Message.class);
        war.addClass(EchoResource.class);
        return TestUtil.finishContainerPrepare(war, null, NotFoundExceptionMapper.class,
                NoMessageBodyWriterFoundFailureExceptionMapper.class, EchoResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(DEP);
    }

    @Test
    public void testAcceptApplicationStar() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request("application/*");
        Response response = request.get();
        try {
            Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertTrue(response.getMediaType().toString().startsWith(MediaType.APPLICATION_XML));
        } finally {
            response.close();
        }
    }

    @Test
    public void testAcceptStarXml() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request("*/xml");
        Response response = request.get();
        try {
            Assertions.assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
        } finally {
            response.close();
        }
    }

    @Test
    public void testAcceptFooBar() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo/missingMBW").queryParam("msg", "Hello world")
                .request("foo/bar");
        Response response = request.get();
        try {
            Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
            Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE.getType(), response.getMediaType().getType());
            Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE.getSubtype(), response.getMediaType().getSubtype());
            Message message = response.readEntity(Message.class);
            Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), message.getStatus());
            Assertions.assertEquals(NoMessageBodyWriterFoundFailure.class.getName(), message.getMessage());
        } finally {
            response.close();
        }
    }

    @Test
    public void Should_ReturnXMLEncodedMessageEntity_When_NotFoundException() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("notFound").request(MediaType.APPLICATION_XML_TYPE);
        Response response = request.get();
        try {
            Assertions.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
            Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE.getType(), response.getMediaType().getType());
            Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE.getSubtype(), response.getMediaType().getSubtype());
            Message message = response.readEntity(Message.class);
            Assertions.assertEquals(Status.NOT_FOUND.getStatusCode(), message.getStatus());
            Assertions.assertEquals(Status.NOT_FOUND.getReasonPhrase(), message.getMessage());
        } finally {
            response.close();
        }
    }
}
