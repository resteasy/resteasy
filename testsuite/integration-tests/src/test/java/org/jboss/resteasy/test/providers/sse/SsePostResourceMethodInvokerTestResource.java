package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventSink;

/**
 * 
 * @author Nicolas NESMON
 *
 */
@Path(SsePostResourceMethodInvokerTestResource.BASE_PATH)
public class SsePostResourceMethodInvokerTestResource {

	@Provider
	@Priority(Integer.MAX_VALUE)
	public static class ExceptionRequestFilter implements ContainerRequestFilter {

		@Override
		public void filter(ContainerRequestContext containerRequestContext) throws IOException {
			throw new IOException();
		}

	}

	public static final String BASE_PATH = "ssePostResourceMethodInvoker";
	public static final String CLOSE_PATH = "close";

	private final Object outputLock = new Object();

	private SseEventSink eventSink;

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void getEventSink(@Context SseEventSink sseEventSink) {
		synchronized (this.outputLock) {
			if (this.eventSink != null) {
				throw new IllegalStateException("Server sink already served.");
			}
			this.eventSink = sseEventSink;
		}
	}

	@DELETE
	@Path(CLOSE_PATH)
	public void close() {
		synchronized (this.outputLock) {
			if (this.eventSink != null) {
				this.eventSink.close();
				this.eventSink = null;
			}
		}
	}

}