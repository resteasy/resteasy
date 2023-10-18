package org.jboss.resteasy.test.profiling;

import java.io.ByteArrayInputStream;
import java.net.URI;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockedProfilingTest {
    @Path("/test")
    public static class CleartextResource {

        @POST
        @Produces("text/plain")
        @Consumes("text/plain")
        @Path("create")
        public String create(String cust) {
            return cust;
        }

    }

    @Test
    public void testUri() throws Exception {
        URI uri = URI.create("/foo");
        Assertions.assertEquals(uri.toString(), "/foo");

        uri = URI.create("foo");
        Assertions.assertEquals(uri.toString(), "foo");
    }

    @Test
    public void testCleartext() throws Exception {
        final int WARMUP = 10;
        final int INTERATIONS = 100;
        //final int WARMUP = 1000;
        //final int INTERATIONS = 1000000;

        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.start();
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(CleartextResource.class);

        MockHttpResponse response = new MockHttpResponse();
        MockHttpRequest request = MockHttpRequest.post("/test/create")
                .header(HttpHeaders.CONTENT_LANGUAGE, "en")
                .header(HttpHeaders.USER_AGENT, "mozilla")
                .header("Custom-Header1", "mozilla")
                .header("Custom-Header2", "mozilla")
                .header("Custom-Header3", "mozilla")
                .header("Custom-Header4", "mozilla")
                .contentType(MediaType.TEXT_PLAIN);
        ByteArrayInputStream stream = new ByteArrayInputStream("hello".getBytes());
        request.setInputStream(stream);

        for (int i = 0; i < WARMUP; i++) {
            deployment.getDispatcher().invoke(request, response);
            stream.reset();
        }
        //      long start = System.currentTimeMillis();
        for (int i = 0; i < INTERATIONS; i++) {
            deployment.getDispatcher().invoke(request, response);
            stream.reset();
        }
        //      long end = System.currentTimeMillis() - start;
        //      System.out.println("Time took: " + end);

    }
}
