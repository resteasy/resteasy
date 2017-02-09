package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;

import org.jboss.resteasy.plugins.providers.sse.SseContextImpl;

@Path("/server-sent-events")
public class SseResource
{

   private final Object outputLock = new Object();

   private SseEventOutput sseEventOutput;

   //TODO: @Inject
   private SseContext sseContext = new SseContextImpl();

   public SseResource()
   {
   }
   @GET
   @Path("single")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public SseEventOutput getMessageQueueSingle() {
      synchronized (outputLock) {       
            sseEventOutput = sseContext.newOutput();
      }
      try {
         OutboundSseEvent.Builder builder = sseContext.newEvent();
         builder.name("Add");
         builder.comment("single event is added");
         builder.data("single");
         sseEventOutput.write(builder.build());
      } catch (Exception e) {
         System.out.println(e);
      }

      return sseEventOutput;
   }
   @GET
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public SseEventOutput getMessageQueue()
   {
      synchronized (outputLock)
      {
         if (sseEventOutput == null)
         {
            sseEventOutput = sseContext.newOutput();
         }
      }

      return sseEventOutput;
   }

   @POST
   public void addMessage(final String message) throws IOException
   {
      if(sseEventOutput == null) {
         return;
      }
      sseEventOutput.write(sseContext.newEvent().name("custom-message").data(String.class, message).build());
   }

   @DELETE
   public void close() throws IOException
   {
      synchronized (outputLock)
      {
         sseEventOutput.close();
         sseEventOutput = sseContext.newOutput();
      }
   }

   private OutboundSseEvent createStatsEvent(final OutboundSseEvent.Builder builder, final int eventId)
   {
      return builder
            .id("" + eventId)
            .data(GreenHouse.class,
                  new GreenHouse(new Date().getTime(), 20 + new Random().nextInt(10), 30 + 20 + new Random()
                        .nextInt(10))).mediaType(MediaType.APPLICATION_JSON_TYPE).build();
   }

   @GET
   @Path("sse/{id}")
   @Produces("text/event-stream")
   public SseEventOutput greenHouseStatus(@PathParam("id") final String id)
   {
      final SseEventOutput output = sseContext.newOutput();

      new Thread()
      {
         public void run()
         {
            try
            {
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 1));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 2));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 3));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 4));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 5));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 6));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 7));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 8));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 9));
               Thread.sleep(1000);
               output.write(createStatsEvent(sseContext.newEvent().comment("greenhouse"), 10));

               output.close();
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      }.start();

      return output;
   }

   @GET
   @Path("domains/{id}")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public SseEventOutput startDomain(@PathParam("id") final String id)
   {
      final SseEventOutput output = sseContext.newOutput();

      new Thread()
      {
         public void run()
         {
            try
            {
               output.write(sseContext.newEvent().name("domain-progress")
                     .data(String.class, "starting domain " + id + " ...").build());
               Thread.sleep(200);
               output.write(sseContext.newEvent().name("domain-progress").data("50%").build());
               Thread.sleep(200);
               output.write(sseContext.newEvent().name("domain-progress").data("60%").build());
               Thread.sleep(200);
               output.write(sseContext.newEvent().name("domain-progress").data("70%").build());
               Thread.sleep(200);
               output.write(sseContext.newEvent().name("domain-progress").data("99%").build());
               Thread.sleep(200);
               output.write(sseContext.newEvent().name("domain-progress").data("Done.").build());
               output.close();

            }
            catch (final InterruptedException e)
            {
               e.printStackTrace();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }.start();

      return output;
   }
}