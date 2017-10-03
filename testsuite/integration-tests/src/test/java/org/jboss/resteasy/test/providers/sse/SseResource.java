package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.plugins.providers.sse.SseConstants;

@Path("/server-sent-events")
public class SseResource
{

   private final Object outputLock = new Object();
   @Context
   private Sse sse;
   private volatile SseEventSink eventSink;
   private SseBroadcaster sseBroadcaster;
   private List<OutboundSseEvent> eventsStore = new ArrayList<OutboundSseEvent>();
   @GET
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void getMessageQueue(@HeaderParam(SseConstants.LAST_EVENT_ID_HEADER) @DefaultValue("-1") int lastEventId, @Context SseEventSink eventSink) {
       synchronized (outputLock) {
           if (this.eventSink != null) {
               throw new IllegalStateException("Server sink already served.");
           }
       }
       this.eventSink = eventSink;
       //replay missed events
       if (lastEventId > -1) {
           synchronized (eventsStore) {
               if (lastEventId + 1 < eventsStore.size()) {
                   List<OutboundSseEvent> missedEvents = eventsStore.subList(lastEventId + 1, eventsStore.size());
                   for (OutboundSseEvent item : missedEvents) {
                        this.eventSink.send(item);
                   }
               }
           }
       }
   }

   @POST
   public void addMessage(final String message) throws IOException {
       if (eventSink == null) {
           throw new IllegalStateException("No client connected.");
       }
       OutboundSseEvent event = null;
       synchronized(eventsStore) {
          event = sse.newEventBuilder().id(Integer.toString(eventsStore.size())).data(message).build();
          eventsStore.add(event);
       }
       eventSink.send(event);
   }
   
   @POST
   @Path("/addMessageAndDisconnect")
   public void addMessageAndDisconnect(final String message) throws IOException, InterruptedException {
        //clear events store first
        eventsStore.clear();
        for (int i = 0; i < 10; i++) {
            OutboundSseEvent event = null;
            synchronized (eventsStore) {
                event = sse.newEventBuilder().id(Integer.toString(eventsStore.size())).data(i + "-" + message).build();
                eventsStore.add(event);
            }
            //disconnect after 3 messages
            if (i == 3) {
                close();
            }
            if (eventSink != null) {
                eventSink.send(event);
            }
            Thread.sleep(250);
        }
   }
   
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
   
   @POST
   @Path("/broadcast")
   public void broadcast(String message) throws IOException {
	   if (sseBroadcaster == null) {
		   sseBroadcaster = sse.newBroadcaster();
	   }
	   sseBroadcaster.broadcast(sse.newEvent(message));
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

   @GET
   @Path("domains/{id}")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void startDomain(@PathParam("id") final String id,
                           @Context SseEventSink sink) {
      new Thread()
      {
         public void run()
         {
            try
            {
               sink.send(sse.newEventBuilder().name("domain-progress")
                     .data(String.class, "starting domain " + id + " ...").build());
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "50%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "60%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "70%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "99%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "Done."))
                  .thenAccept((Object obj) -> {sink.close();});
            }
            catch (final InterruptedException e)
            {
               e.printStackTrace();
            }
         }
      }.start();
   }

   @GET
   @Path("/error")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void testErrorConsumer(@Context SseEventSink eventSink) {
       throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
   }

}