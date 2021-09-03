package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/sse")
public class AnotherSseResource
{

   private final Object outputLock = new Object();
   private final Object sseBroadcasterLock = new Object();

   @Context
   private Sse sse;

   private volatile SseEventSink eventSink;

   private volatile SseBroadcaster sseBroadcaster;

   private final SseResource sseResource;

   public AnotherSseResource(final SseResource sseResource) {
      this.sseResource = sseResource;
   }

   @GET
   @Path("/subscribe")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void subscribe(@Context SseEventSink sink) throws IOException
   {

      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      eventSink = sink;
      synchronized (this.sseBroadcasterLock) {
         //subscribe
         if (sseBroadcaster == null)
         {
            sseBroadcaster = sse.newBroadcaster();
         }
      }
      this.sseResource.subscribe(sink);
      sseBroadcaster.register(sink);
   }

   @POST
   @Path("/broadcast")
   public void broadcast(String message) throws IOException
   {
      if (this.sseBroadcaster == null)
      {
         throw new IllegalStateException("No Sse broadcaster created.");
      }
      this.sseBroadcaster.broadcast(sse.newEvent(message));
   }

   @DELETE
   @Produces(MediaType.TEXT_PLAIN)
   public boolean close() throws IOException
   {
      synchronized (outputLock)
      {
         if (eventSink != null)
         {
            try (SseEventSink sink = eventSink)
            {
               //do nothing and this is intented to test eventSink's try-with-resources autoCloseable
            }
         }
      }
      return eventSink.isClosed();
   }

}
