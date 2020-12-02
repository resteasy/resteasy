package org.jboss.resteasy.test.providers.sse.resource;

import org.junit.Assert;

import javax.ejb.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import java.util.concurrent.TimeUnit;

@Singleton
@Path("/reconnect")
public class SseReconnectResource {

   private volatile boolean isServiceAvailable = false;

   private volatile long startTime = 0L;
   private volatile long endTime = 0L;
   private volatile long lastEventDeliveryTime;

   @GET
   @Path("/defaultReconnectDelay")
   @Produces(MediaType.TEXT_PLAIN)
   public Response reconnectDelayNotSet(@Context Sse sse, @Context SseEventSink sseEventSink) {
      OutboundSseEvent event = sse.newEventBuilder().id("1").data("test").build();
      return Response.ok(event.getReconnectDelay()).build();
   }

   @GET
   @Path("/reconnectDelaySet")
   @Produces(MediaType.TEXT_PLAIN)
   public Response reconnectDelaySet(@Context Sse sse, @Context SseEventSink sseEventSink) {
      OutboundSseEvent event = sse.newEventBuilder().id("1").data("test").reconnectDelay(1000L).build();
      return Response.ok(event.getReconnectDelay()).build();
   }

   @GET
   @Path("/unavailable")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void sendMessage(@Context SseEventSink sink, @Context Sse sse)
   {
      if (!isServiceAvailable)
      {
         isServiceAvailable = true;
         startTime = System.currentTimeMillis();
         throw new WebApplicationException(Response.status(503).header(HttpHeaders.RETRY_AFTER, String.valueOf(2))
               .build());
      }
      else
      {
         endTime = System.currentTimeMillis() - startTime;
         long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime);
         Assert.assertTrue(elapsedSeconds >= 2);
         try (SseEventSink s = sink)
         {
            s.send(sse.newEvent("ServiceAvailable"));
            isServiceAvailable = false;
         }
      }
   }

   @GET
   @Path("/testReconnectDelayIsUsed")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void testReconnectDelay(@Context SseEventSink sseEventSink, @Context Sse sse,
         @HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("") String lastEventId)
   {
      switch (lastEventId)
      {
         case "0" :
            checkReconnectDelay(TimeUnit.SECONDS.toMillis(3));
            try (SseEventSink s = sseEventSink)
            {
               sendEvent(s, sse, "1", null);
            }
            break;
         case "1" :
            checkReconnectDelay(TimeUnit.SECONDS.toMillis(3));
            throw new WebApplicationException(599);
         default :
            try (SseEventSink s = sseEventSink)
            {
               sendEvent(s, sse, "0", TimeUnit.SECONDS.toMillis(3));
            }
            break;
      }
   }

   static int retry_cnt = 2;
   @GET
   @Path("/unavailableAfterRetry")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void failAfterRetry(@Context SseEventSink sink, @Context Sse sse)
   {
      if (retry_cnt <= 0) {
         startTime = System.currentTimeMillis();
         throw new WebApplicationException(Response.status(503)
                 .build());
      } else {
         throw new WebApplicationException(Response.status(503)
                 .header(HttpHeaders.RETRY_AFTER, String.valueOf(retry_cnt--))
                 .build());
      }
   }

   private void sendEvent(SseEventSink sseEventSink, Sse sse, String eventId, Long reconnectDelayInMs)
   {
      OutboundSseEvent.Builder outboundSseEventBuilder = sse.newEventBuilder().data("Event " + eventId).id(eventId);
      if (reconnectDelayInMs != null)
      {
         outboundSseEventBuilder.reconnectDelay(reconnectDelayInMs);
      }
      sseEventSink.send(outboundSseEventBuilder.build());
      lastEventDeliveryTime = System.currentTimeMillis();
   }

   private void checkReconnectDelay(long expectedDelayInMs)
   {
      long currentDelayInMs = System.currentTimeMillis() - lastEventDeliveryTime;
      Assert.assertTrue(currentDelayInMs >= expectedDelayInMs);
   }

}
