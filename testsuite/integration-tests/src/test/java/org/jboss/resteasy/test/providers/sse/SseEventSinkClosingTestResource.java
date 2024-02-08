package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.ResteasyContext;

/**
 *
 * @author Nicolas NESMON
 *
 */
@Path(SseEventSinkClosingTestResource.BASE_PATH)
public class SseEventSinkClosingTestResource {

    @Provider
    public static class ContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {

        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
                throws IOException {
            AtomicInteger responseFilterInvocationCounter = (AtomicInteger) requestContext
                    .getProperty(RESPONSE_FILTER_INVOCATION_COUNTER_PROPERTY);
            if (responseFilterInvocationCounter != null) {
                responseFilterInvocationCounter.incrementAndGet();
            }
        }

        @Override
        public void filter(ContainerRequestContext containerRequestContext) throws IOException {
            ResteasyContext.pushContext(ContainerRequestContext.class, containerRequestContext);
        }

    }

    public static final String BASE_PATH = "sseEventSinkClosing";
    public static final String SEND_AND_CLOSE_PATH = "sendAndClose";
    public static final String CLOSE_WITHOUT_SENDING_PATH = "closeWithoutSending";
    public static final String GET_RESPONSE_FILTER_INVOCATION_COUNT_PATH = "getResponseFilterInvocationCount";
    public static final String RESET_RESPONSE_FILTER_INVOCATION_COUNT_PATH = "resetResponseFilterInvocationCount";

    private static final String RESPONSE_FILTER_INVOCATION_COUNTER_PROPERTY = "counter";

    private final AtomicInteger responseFilterCountInvocation = new AtomicInteger();

    @GET
    @Path(SEND_AND_CLOSE_PATH)
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sendAndClose(@Context ContainerRequestContext requestContext, @Context Sse sse,
            @Context SseEventSink sseEventSink) throws IOException {
        requestContext.setProperty(RESPONSE_FILTER_INVOCATION_COUNTER_PROPERTY, this.responseFilterCountInvocation);
        sseEventSink.send(sse.newEvent("message"));
        sseEventSink.close();
    }

    @GET
    @Path(CLOSE_WITHOUT_SENDING_PATH)
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void closedWithoutSending(@Context ContainerRequestContext requestContext, @Context Sse sse,
            @Context SseEventSink sseEventSink) throws IOException {
        requestContext.setProperty(RESPONSE_FILTER_INVOCATION_COUNTER_PROPERTY, this.responseFilterCountInvocation);
        sseEventSink.close();
    }

    @GET
    @Path(GET_RESPONSE_FILTER_INVOCATION_COUNT_PATH)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResponseFilterInvocationCount() {
        return Response.ok(this.responseFilterCountInvocation.get()).build();
    }

    @DELETE
    @Path(RESET_RESPONSE_FILTER_INVOCATION_COUNT_PATH)
    public void resetResponseFilterInvocationCount() {
        this.responseFilterCountInvocation.set(0);
    }

}
