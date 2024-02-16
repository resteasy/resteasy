package org.jboss.resteasy.test.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Configurable;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * @tpSubChapter Profiler helper tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Use to benchmark with a profiler
 * @tpSince RESTEasy 4.1.1
 */
public class BenchmarkTest {

    private static Dispatcher dispatcher;

    @BeforeAll
    public static void BeforeClass() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addPerRequestResource(HelloResource.class);
    }

    @BeforeEach
    public void before() {
        ResteasyContext.getContextDataMap().put(Configurable.class, dispatcher.getProviderFactory());
    }

    @Path("/hello")
    public static class HelloResource {
        @GET
        @Produces("text/plain")
        public String get() {
            return "hello world";
        }

        @POST
        @Produces("text/plain")
        @Consumes("text/plain")
        public String post(String name) {
            return "Hello " + name;
        }

        @GET
        @Path("{id}")
        @Produces("text/plain")
        public String getPath(@PathParam("id") int id) {
            return "hello world " + id;
        }

    }

    private static final int ITERATIONS = 1000000;

    //@Test
    public void runPathGet() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            testPathGet();
        }
        long end = System.currentTimeMillis() - start;
        //System.out.println("Took " + end);
    }

    public void testPathGet() {
        MockHttpRequest request = MockHttpRequest.create("GET", "/hello/1", "", "");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

    }

    public void testPlainGet() {
        MockHttpRequest request = MockHttpRequest.create("GET", "/hello", "", "");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

    }

    public void testPlainPost() {
        MockHttpRequest request = MockHttpRequest.create("GET", "/hello", "", "");
        request.contentType("text/plain").content("world".getBytes());
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

    }

}
