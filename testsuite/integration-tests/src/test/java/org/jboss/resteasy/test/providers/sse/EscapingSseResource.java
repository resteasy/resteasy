package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/sse-escaping")
public class EscapingSseResource
{

   @Context
   private Sse sse;

   @GET
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void subscribe(@Context SseEventSink sink) throws IOException
   {

      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      try (SseEventSink s = sink)
      {
         CompletableFuture.allOf(s.send(sse.newEvent("foo1\nbar")).toCompletableFuture(),
               s.send(sse.newEvent("foo2\r\nbar")).toCompletableFuture(),
               s.send(sse.newEvent("foo3\rbar")).toCompletableFuture()).get();
      }
      catch (Exception e)
      {
         throw new IOException(e);
      }
   }
}
