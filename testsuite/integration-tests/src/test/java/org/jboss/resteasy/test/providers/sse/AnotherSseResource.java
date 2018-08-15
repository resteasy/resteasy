package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
   private final Object sseBroadcasterLock = new Object();

   @Context
   private Sse sse;
   
   private volatile SseEventSink eventSink;

   private volatile SseBroadcaster sseBroadcaster;
   
   private final SseResource sseResource;
   
   public AnotherSseResource(SseResource sseResource) {
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
