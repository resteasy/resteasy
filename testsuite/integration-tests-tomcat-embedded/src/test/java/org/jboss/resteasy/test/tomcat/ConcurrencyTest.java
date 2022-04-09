package org.jboss.resteasy.test.tomcat;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.reactor.MonoRxInvoker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * These tests are designed to catch concurrency issues in RestEasy when Tomcat is the servlet container.
 * They were originally written to expose <a href="https://issues.redhat.com/browse/RESTEASY-3071">RESTEASY-3071</a>
 * and almost certainly are machine-specific.  However, they did consistently at the time before the changes to
 * {@link org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper}.
 */
public class ConcurrencyTest {
    private static final Logger log = Logger.getLogger(ConcurrencyTest.class);

    private static Tomcat tomcat;
    private static ResteasyClient client;

    @BeforeClass
    public static void beforeClass() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(0);
        tomcat.getConnector();
        final Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        ctx.addParameter("javax.ws.rs.Application", JaxrsApplication.class.getName());
        Tomcat.addServlet(ctx, "rest-easy-servlet", new HttpServlet30Dispatcher());
        ctx.addServletMappingDecoded("/*", "rest-easy-servlet");
        tomcat.start();
        client = (ResteasyClient)ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        if (client != null) {
            try {
                client.close();
            } catch (final Exception e) {
                log.error("Problem closing client.", e);
            }
        }
        if (tomcat != null) {
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (final Exception e) {
                log.error("Problem closing tomcat.", e);
            }
        }
    }

    @Test
    /**
     * Exposed <a href="https://issues.redhat.com/browse/RESTEASY-3071">RESTEASY-3071</a>.
     */
    public void testLargeResponsePayload() {
        final int numberOfCalls = 50;
        final int numberOfItems = 50;
        final String url = generateUrl("/stream/async/reactive/" + numberOfItems);

        // Deserializing to strings first gives better error message for the RESTEASY-3071 problem.
        final List<String> responses =
            Flux.range(0, numberOfCalls)
                .flatMap(i ->
                    client.target(url)
                        .request()
                        .rx(MonoRxInvoker.class)
                        .post(Entity.text("Test Request " + i), String.class)
                ).collectList()
                .block();

        assertThat(responses, hasSize(numberOfCalls));

        final ObjectMapper mapper = new ObjectMapper();

        final List<List<AsyncStreaming.ResponseData>> deserializedResponses =
            responses.stream()
                .map(s -> {
                    try {
                        return mapper.readValue(s, new TypeReference<List<AsyncStreaming.ResponseData>>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        deserializedResponses.forEach(dataList -> {
            assertThat(dataList, hasSize(50));
            dataList.forEach(responseData -> {
                assertThat(responseData.getHeaders(), notNullValue());
                assertThat(responseData.getResponseBody(), startsWith("Test Request"));
                assertThat(responseData.getHeaders().keySet(), hasSize(750));
            });
        });
    }

    private static String generateUrl(final String path) {
        final String absolutePath = path.startsWith("/") ? path : "/" + path;
        return tomcat.getConnector().getScheme() + "://"
            + tomcat.getHost().getName() + ":" + tomcat.getConnector().getLocalPort()
            + absolutePath;
    }

    public static class JaxrsApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return new HashSet<>(Collections.singletonList(AsyncStreaming.class));
        }
    }

    @Path("/stream")
    public static class AsyncStreaming {

        @POST
        @Path("/async/reactive/{noOfItems}")
        @Produces(MediaType.APPLICATION_JSON)
        public Mono<List<ResponseData>> echoAsyncReactive(
            @PathParam("noOfItems") final int noOfItems,
            final String requestBody
        ) {
            final Map<String, String> headers = IntStream.range(0, 750)
                .mapToObj(i -> new String[]{ "testHeader" + i, "testHeaderValue" + i})
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));

            return Flux.range(0, noOfItems)
                .map(idx -> new ResponseData(idx, requestBody + "_response", headers))
                .flatMap(data -> Mono.just(data).delayElement(Duration.ofMillis(1)))
                .collectList();
        }

        public static class ResponseData {
            private Integer id;
            private String responseBody;
            private Map<String, String> headers;

            public ResponseData() {
            }

            public ResponseData(final Integer id,
                                final String responseBody,
                                final Map<String, String> headers) {
                this.id = id;
                this.responseBody = responseBody;
                this.headers = headers;
            }

            public Integer getId() {
                return id;
            }

            public String getResponseBody() {
                return responseBody;
            }

            public Map<String, String> getHeaders() {
                return headers;
            }
        }
    }
}
