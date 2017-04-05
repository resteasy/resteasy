package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;

public class SseImpl implements Sse
{
   //spec leader said there will be a request scope broadcaster and a static broadcaster
   //implement a static boradcaster first
   public static SseBroadcaster  broadCaster= new SseBroadcasterImpl();
   @Override
   public OutboundSseEvent.Builder newEventBuilder()
   {
      return new OutboundSseEventImpl.BuilderImpl();
   }

   @Override
   public SseBroadcaster newBroadcaster()
   {
      return broadCaster;
   }
}
