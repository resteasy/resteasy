package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;

public class SseImpl implements Sse
{

	@Override
   public OutboundSseEvent.Builder newEventBuilder()
   {
      return new OutboundSseEventImpl.BuilderImpl();
   }

   @Override
   public SseBroadcaster newBroadcaster()
   {
      return new SseBroadcasterImpl();
   }
}
