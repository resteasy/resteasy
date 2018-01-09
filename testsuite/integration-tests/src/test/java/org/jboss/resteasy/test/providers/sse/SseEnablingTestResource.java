package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

/**
 * 
 * @author Nicolas NESMON
 *
 */
@Path(SseEnablingTestResource.PATH)
public class SseEnablingTestResource {

	public static final String PATH = "sseEnablingTest";
	public static final String RESOURCE_METHOD_1_PATH = "resourceMethod1";
	public static final String RESOURCE_METHOD_2_PATH = "resourceMethod2";
	public static final String RESOURCE_METHOD_3_PATH = "resourceMethod3";
	public static final String RESOURCE_METHOD_4_PATH = "resourceMethod4";
	public static final String RESOURCE_METHOD_5_PATH = "resourceMethod5";
	public static final String RESOURCE_METHOD_6_PATH = "resourceMethod6";

	public static String OK_MESSAGE = "OK";

	// Will be called by client with "Accept: application/xml,
	// text/event-stream"
	@Path(RESOURCE_METHOD_1_PATH)
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response resourceMethod_1(@Context SseEventSink sseEventSink) {
		if (sseEventSink == null) {
			return Response.ok(OK_MESSAGE).build();
		}
		throw new InternalServerErrorException();
	}

	// Will be called by client with "Accept: application/xml;q=0.9,
	// text/event-stream;q=0.5"
	@Path(RESOURCE_METHOD_2_PATH)
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.SERVER_SENT_EVENTS })
	public Response resourceMethod_2(@Context SseEventSink sseEventSink) {
		if (sseEventSink == null) {
			return Response.ok(OK_MESSAGE).build();
		}
		throw new InternalServerErrorException();
	}

	// Will be called by client with "Accept: application/xml,
	// text/event-stream"
	@Path(RESOURCE_METHOD_3_PATH)
	@GET
	@Produces({ "application/xml;qs=0.9", "text/event-stream;qs=0.5" })
	public Response resourceMethod_3(@Context SseEventSink sseEventSink) {
		if (sseEventSink == null) {
			return Response.ok(OK_MESSAGE).build();
		}
		throw new InternalServerErrorException();
	}

	// Will be called by client with "Accept: application/xml;q=0.9,
	// application/json;q=0.5"
	@Path(RESOURCE_METHOD_4_PATH)
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.SERVER_SENT_EVENTS })
	public Response resourceMethod_4(@Context SseEventSink sseEventSink) {
		if (sseEventSink == null) {
			return Response.ok(OK_MESSAGE).build();
		}
		throw new InternalServerErrorException();
	}

	// Will be called by client with "Accept: text/event-stream"
	@Path(RESOURCE_METHOD_5_PATH)
	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public Response resourceMethod_5() {
		return Response.noContent().build();
	}
	
	// Will be called by client with "Accept: text/event-stream"
	@Path(RESOURCE_METHOD_6_PATH)
	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void resourceMethod_6(@Context SseEventSink sseEventSink, @Context Sse sse) {
		sseEventSink.send(sse.newEvent("data"));
		sseEventSink.close();
	}

}
