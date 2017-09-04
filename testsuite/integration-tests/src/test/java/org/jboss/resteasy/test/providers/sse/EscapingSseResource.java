package org.jboss.resteasy.test.providers.sse;
import java.io.IOException;

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
   public void subscribe(@Context SseEventSink sink) throws IOException {

      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      try(SseEventSink s = sink) 
      {
         s.send(sse.newEvent("foo\nbar"));
         s.send(sse.newEvent("foo\r\nbar"));
         s.send(sse.newEvent("foo\rbar"));
      }
   }
}
