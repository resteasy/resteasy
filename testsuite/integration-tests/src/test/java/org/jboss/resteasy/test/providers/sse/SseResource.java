package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.plugins.providers.sse.SseEventOutputImpl;
import org.jboss.resteasy.plugins.providers.sse.SseEventProvider;
import org.jboss.resteasy.plugins.providers.sse.SseImpl;

@Path("/server-sent-events")
public class SseResource
{

   private final Object outputLock = new Object();
   private final Sse sse = new SseImpl(); //TODO inject this
   private volatile SseEventSink eventSink;

   @GET
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void getMessageQueue(@Context SseEventSink eventSink) {
       synchronized (outputLock) {
           if (this.eventSink != null) {
               throw new IllegalStateException("Server sink already served.");
           }
//           this.eventSink = eventSink;
           this.eventSink = new SseEventOutputImpl(new SseEventProvider()); //TODO replace this with @Context injected value
       }
   }

   @POST
   public void addMessage(final String message) throws IOException {
       if (eventSink == null) {
           throw new IllegalStateException("No client connected.");
       }
       eventSink.onNext(sse.newEvent("custom-message"));
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
                           @Context SseEventSink eventSink) {
      final SseEventSink eventSink2 = new SseEventOutputImpl(new SseEventProvider()); //TODO replace this with @Context injected value
      new Thread()
      {
         public void run()
         {
            try
            {
               eventSink2.onNext(sse.newEventBuilder().name("domain-progress")
                     .data(String.class, "starting domain " + id + " ...").build());
               Thread.sleep(200);
               eventSink2.onNext(sse.newEvent("domain-progress", "50%"));
               Thread.sleep(200);
               eventSink2.onNext(sse.newEvent("domain-progress", "60%"));
               Thread.sleep(200);
               eventSink2.onNext(sse.newEvent("domain-progress", "70%"));
               Thread.sleep(200);
               eventSink2.onNext(sse.newEvent("domain-progress", "99%"));
               Thread.sleep(200);
               eventSink2.onNext(sse.newEvent("domain-progress", "Done."));
               eventSink2.close();
            }
            catch (final InterruptedException | IOException e)
            {
               e.printStackTrace();
            }
         }
      }.start();
   }

}