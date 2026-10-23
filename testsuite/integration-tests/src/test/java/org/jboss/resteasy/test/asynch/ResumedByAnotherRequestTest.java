/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.asynch;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that resuming a suspended request from the resource method of another request, on that request's thread, does
 * not remove the context of the resuming request. This is the pattern shown in the {@link AsyncResponse} javadoc.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResumedByAnotherRequestTest {

    @ArquillianResource
    public URL url;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, ResumedByAnotherRequestTest.class.getSimpleName() + ".war")
                .addClasses(
                        ResumeResource.class,
                        SuspendedResponses.class,
                        EchoFilter.class,
                        TestApplication.class);
    }

    /**
     * Request A suspends. Request B resumes A from its resource method, then reads its own {@link UriInfo}. Both
     * requests must see their own context: A in its response filter, and B in its resource method after resuming and
     * in its response filter.
     */
    @Test
    public void resumedByAnotherRequest() throws Exception {
        final String id = UUID.randomUUID().toString();
        try (Client client = ClientBuilder.newClient()) {
            final Future<Response> suspended = client.target(TestUtil.generateUri(url, "/request/suspend/%s".formatted(id)))
                    .request(MediaType.TEXT_PLAIN)
                    .async()
                    .get();

            try (Response resumer = client.target(TestUtil.generateUri(url, "/request/resume/%s".formatted(id)))
                    .request(MediaType.TEXT_PLAIN)
                    .get()) {
                final String entity = resumer.readEntity(String.class);
                Assertions.assertEquals(200, resumer.getStatus(), entity);
                assertPath("resume", id, entity, "Request B's resource method after resuming request A");
                assertPath("resume", id, resumer.getHeaderString(EchoFilter.HEADER), "Request B's response filter");
            }

            try (Response resumed = suspended.get(10, TimeUnit.SECONDS)) {
                final String entity = resumed.readEntity(String.class);
                Assertions.assertEquals(200, resumed.getStatus(), entity);
                Assertions.assertEquals("resumed", entity);
                assertPath("suspend", id, resumed.getHeaderString(EchoFilter.HEADER), "Request A's response filter");
            }
        }
    }

    private static void assertPath(final String operation, final String id, final String actual, final String where) {
        final String expected = "request/%s/%s".formatted(operation, id);
        Assertions.assertTrue(actual != null && actual.endsWith(expected),
                () -> "%s: expected the path to end with %s but was %s".formatted(where, expected, actual));
    }

    public static class SuspendedResponses {
        private static final Map<String, AsyncResponse> RESPONSES = new ConcurrentHashMap<>();

        static void put(final String id, final AsyncResponse asyncResponse) {
            RESPONSES.put(id, asyncResponse);
        }

        /**
         * Removes the suspended response, waiting up to 5 seconds for the request to be suspended.
         */
        static AsyncResponse remove(final String id) throws InterruptedException {
            final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            AsyncResponse asyncResponse = RESPONSES.remove(id);
            while (asyncResponse == null && System.nanoTime() < end) {
                TimeUnit.MILLISECONDS.sleep(10);
                asyncResponse = RESPONSES.remove(id);
            }
            return asyncResponse;
        }
    }

    @Path("/request")
    @Produces(MediaType.TEXT_PLAIN)
    public static class ResumeResource {

        @Context
        UriInfo uriInfo;

        @GET
        @Path("/suspend/{id}")
        public void suspend(@PathParam("id") final String id, final AsyncResponse asyncResponse) {
            SuspendedResponses.put(id, asyncResponse);
        }

        @GET
        @Path("/resume/{id}")
        public Response resume(@PathParam("id") final String id) throws InterruptedException {
            final AsyncResponse asyncResponse = SuspendedResponses.remove(id);
            if (asyncResponse == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Request %s was not suspended".formatted(id))
                        .build();
            }
            asyncResponse.resume("resumed");
            // The context of this request must still be available after resuming the other request on this thread
            return Response.ok(uriInfo.getPath()).build();
        }
    }

    /**
     * Echoes the path of the request the injected {@link UriInfo} resolves to.
     */
    @Provider
    public static class EchoFilter implements ContainerResponseFilter {
        static final String HEADER = "echo-path";

        @Context
        UriInfo uriInfo;

        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
            String path;
            try {
                path = uriInfo.getPath();
            } catch (RuntimeException e) {
                path = "failed(%s)".formatted(e.getMessage());
            }
            responseContext.getHeaders().add(HEADER, path);
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
    }
}
