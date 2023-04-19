package org.jboss.resteasy.test.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configurable;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @tpSubChapter Profiler helper tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Use to benchmark with a profiler
 * @tpSince RESTEasy 4.1.1
 */
public class BenchmarkTest {

    private static Dispatcher dispatcher;

    @BeforeClass
    public static void BeforeClass() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addPerRequestResource(HelloResource.class);
    }

    @Before
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
