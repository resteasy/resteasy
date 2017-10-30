package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

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
