package org.jboss.resteasy.test.providers.sse;
import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("/sse")
public class AnotherSseResource
{

   private final Object outputLock = new Object();
   @Context
   private Sse sse;
   private volatile SseEventSink eventSink;
   private SseBroadcaster sseBroadcaster;

   @GET
   @Path("/subscribe")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void subscribe(@Context SseEventSink sink) throws IOException {
	   if (sink == null) {
           throw new IllegalStateException("No client connected.");
       }
       //subscribe
	   if (sseBroadcaster == null) {
		   sseBroadcaster = sse.newBroadcaster();
	   }
	   sseBroadcaster.register(sink);	  
   }
  
   @DELETE
   public void close() throws IOException {
       synchronized (outputLock) {
           if (eventSink != null) {
               eventSink.close();
               eventSink = null;
           }
       }
   }

}
