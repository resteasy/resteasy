package org.jboss.resteasy.test.providers.sse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author Nicolas NESMON
 *
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseEnablingTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseEnablingTest.class.getSimpleName());
        war.addClass(SseEnablingTestResource.class);
        return TestUtil.finishContainerPrepare(war, null, SseEnablingTestResource.class);
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(SseEnablingTest.class.getSimpleName());
    }

    // Client
    // Accept: application/xml, text/event-stream
    //
    // Server
    // @GET
    // @Produces(MediaType.APPLICATION_XML)
    // public Response resourceMethod(@Context SseEventSink sseEventSink){
    // ...
    // }
    @Test
    public void testAcceptXmlSseServerXml() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL()).path(SseEnablingTestResource.PATH);
            try (Response response = baseTarget.path(SseEnablingTestResource.RESOURCE_METHOD_1_PATH)
                    .request(MediaType.APPLICATION_XML_TYPE, MediaType.SERVER_SENT_EVENTS_TYPE).get()) {
                checkResponse(response);
            }
        } finally {
            client.close();
        }
    }

    // Client
    // Accept: application/xml;q=0.9, text/event-stream;q=0.5
    //
    // Server
    // @GET
    // @Produces({MediaType.APPLICATION_XML, MediaType.SERVER_SENT_EVENTS})
    // public Response resourceMethod(@Context SseEventSink sseEventSink){
    // ...
    // }
    @Test
    public void testSseAcceptWithQualityFactors() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL()).path(SseEnablingTestResource.PATH);
            try (Response response = baseTarget.path(SseEnablingTestResource.RESOURCE_METHOD_2_PATH)
                    .request("application/xml;q=0.9", "text/event-stream;q=0.5").get()) {
                checkResponse(response);
            }
        } finally {
            client.close();
        }
    }

    // Client
    // Accept: application/xml, text/event-stream
    //
    // Server
    // @GET
    // @Produces({"application/xml;qs=0.9", "text/event-stream;qs=0.5"})
    // public Response resourceMethod(@Context SseEventSink sseEventSink){
    // ...
    // }
    @Test
    public void testSseServerQualityFactors() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL()).path(SseEnablingTestResource.PATH);
            try (Response response = baseTarget.path(SseEnablingTestResource.RESOURCE_METHOD_3_PATH)
                    .request(MediaType.APPLICATION_XML_TYPE, MediaType.SERVER_SENT_EVENTS_TYPE).get()) {
                checkResponse(response);
            }
        } finally {
            client.close();
        }
    }

    // Client
    // Accept: application/xml;q=0.9, application/json;q=0.5
    //
    // Server
    // @GET
    // @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
    // MediaType.SERVER_SENT_EVENTS})
    // public Response resourceMethod(@Context SseEventSink sseEventSink){
    // ...
    // }
    @Test
    public void testAcceptXmlJsonServerXmlJsonSse() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL()).path(SseEnablingTestResource.PATH);
            try (Response response = baseTarget.path(SseEnablingTestResource.RESOURCE_METHOD_4_PATH)
                    .request("application/xml;q=0.9", "application/json;q=0.5").get()) {
                checkResponse(response);
            }
        } finally {
            client.close();
        }
    }

    // Client
    // Accept: text/event-stream
    //
    // Server
    // @GET
    // @Produces(MediaType.SERVER_SENT_EVENTS)
    // public Response resourceMethod(){
    // ...
    // }
    @Test
    public void testAcceptSseServerSseNoContent() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL()).path(SseEnablingTestResource.PATH);
            int responseSTatus = baseTarget.path(SseEnablingTestResource.RESOURCE_METHOD_5_PATH)
                    .request(MediaType.SERVER_SENT_EVENTS_TYPE).get().getStatus();
            Assertions.assertEquals(Status.NO_CONTENT.getStatusCode(), responseSTatus);
        } finally {
            client.close();
        }
    }

    // Client
    // Accept: text/event-stream
    //
    // Server
    // @GET
    // @Produces(MediaType.SERVER_SENT_EVENTS)
    // public void resourceMethod(@Context SseEventSink sseEventSink){
    // ...
    // }
    @Test
    public void testAcceptSseServerSseEvent() throws Exception {

        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL()).path(SseEnablingTestResource.PATH);
            try (SseEventSource eventSource = SseEventSource
                    .target(baseTarget.path(SseEnablingTestResource.RESOURCE_METHOD_6_PATH)).build()) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                eventSource.register(event -> {
                    countDownLatch.countDown();
                }, e -> {
                    throw new RuntimeException(e);
                });
                eventSource.open();
                boolean result = countDownLatch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(result, "Waiting for event to be delivered has timed out.");
            }
        } finally {
            client.close();
        }
    }

    private void checkResponse(Response response) {
        Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
        MediaType contentType = response.getMediaType();
        Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE.getType(), contentType.getType());
        Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE.getSubtype(), contentType.getSubtype());
        Assertions.assertEquals(SseEnablingTestResource.OK_MESSAGE, response.readEntity(String.class));
    }

}
