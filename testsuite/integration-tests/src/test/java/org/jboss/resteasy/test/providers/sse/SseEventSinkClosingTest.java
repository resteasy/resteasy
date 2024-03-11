package org.jboss.resteasy.test.providers.sse;

import java.util.Arrays;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseEventSinkClosingTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseEventSinkClosingTest.class.getSimpleName());
        war.addClass(SseEventSinkClosingTestResource.class);
        war.addClass(SseEventSinkClosingTestResource.ContainerFilter.class);
        return TestUtil.finishContainerPrepare(war, null, Arrays.asList(SseEventSinkClosingTestResource.class),
                SseEventSinkClosingTestResource.ContainerFilter.class);
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(SseEventSinkClosingTest.class.getSimpleName());
    }

    @AfterEach
    public void reset() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            client.target(generateURL()).path(SseEventSinkClosingTestResource.BASE_PATH)
                    .path(SseEventSinkClosingTestResource.RESET_RESPONSE_FILTER_INVOCATION_COUNT_PATH).request()
                    .delete();
        } finally {
            client.close();
        }
    }

    @Test
    public void testFilterForEventSent() throws Exception {

        Client client = ClientBuilder.newClient();
        try {

            WebTarget baseTarget = client.target(generateURL()).path(SseEventSinkClosingTestResource.BASE_PATH);

            try (Response response = baseTarget.path(SseEventSinkClosingTestResource.SEND_AND_CLOSE_PATH)
                    .request(MediaType.SERVER_SENT_EVENTS_TYPE).get()) {
                Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
            }

            try (Response response = baseTarget
                    .path(SseEventSinkClosingTestResource.GET_RESPONSE_FILTER_INVOCATION_COUNT_PATH)
                    .request(MediaType.TEXT_PLAIN_TYPE).get()) {
                Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
                Assertions.assertEquals(Integer.valueOf(1), response.readEntity(Integer.class));
            }

        } finally {
            client.close();
        }

    }

    @Test
    public void testFilterForMethodReturn() throws Exception {

        Client client = ClientBuilder.newClient();
        try {

            WebTarget baseTarget = client.target(generateURL()).path(SseEventSinkClosingTestResource.BASE_PATH);

            try (Response response = baseTarget.path(SseEventSinkClosingTestResource.CLOSE_WITHOUT_SENDING_PATH)
                    .request(MediaType.SERVER_SENT_EVENTS_TYPE).get()) {
                Assertions.assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
            }

            try (Response response = baseTarget
                    .path(SseEventSinkClosingTestResource.GET_RESPONSE_FILTER_INVOCATION_COUNT_PATH)
                    .request(MediaType.TEXT_PLAIN_TYPE).get()) {
                Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
                Assertions.assertEquals(Integer.valueOf(1), response.readEntity(Integer.class));
            }

        } finally {
            client.close();
        }

    }

}
