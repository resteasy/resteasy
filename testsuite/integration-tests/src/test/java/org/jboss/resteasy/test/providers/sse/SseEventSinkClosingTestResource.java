package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

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
			ResteasyProviderFactory.pushContext(ContainerRequestContext.class, containerRequestContext);
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
			@Context SseEventSink sseEventSink) {
		requestContext.setProperty(RESPONSE_FILTER_INVOCATION_COUNTER_PROPERTY, this.responseFilterCountInvocation);
		sseEventSink.send(sse.newEvent("message"));
		sseEventSink.close();
	}

	@GET
	@Path(CLOSE_WITHOUT_SENDING_PATH)
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void closedWithoutSending(@Context ContainerRequestContext requestContext, @Context Sse sse,
			@Context SseEventSink sseEventSink) {
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
