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

      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      eventSink = sink;
      //subscribe
      if (sseBroadcaster == null)
      {
         sseBroadcaster = sse.newBroadcaster();
      }
      sseBroadcaster.register(sink);  
   }
  
   @DELETE
   @Produces(MediaType.TEXT_PLAIN)
   public boolean close() throws IOException {
       synchronized (outputLock) {
           if (eventSink != null) {
              try (SseEventSink sink = eventSink) {
                 //do nothing and this is intented to test eventSink's try-with-resources autoCloseable
              }
           }
       }
       return eventSink.isClosed();
   }

}
