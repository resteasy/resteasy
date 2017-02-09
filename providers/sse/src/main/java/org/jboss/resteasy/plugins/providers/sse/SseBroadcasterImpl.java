package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventOutput;

public class SseBroadcasterImpl implements SseBroadcaster
{
   private final Set<SseEventOutput> outputs = Collections.newSetFromMap(new ConcurrentHashMap<SseEventOutput, Boolean>());
   private final Set<Listener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<Listener, Boolean>());

   @Override
   public boolean register(Listener listener)
   {
      return listeners.add(listener);
   }

   @Override
   public boolean register(SseEventOutput output)
   {
      return outputs.add(output);
   }

   @Override
   public void broadcast(OutboundSseEvent event)
   {
      for (final SseEventOutput output : outputs)
      {
         try
         {
            output.write(event);
         }
         catch (final IOException ex)
         {
            for (Listener listener : listeners)
            {
               listener.onException(output, ex);
            }
         }
      }
   }

   @Override
   public void close()
   {
      for (final SseEventOutput output : outputs)
      {
         try
         {
            output.close();
            for (Listener listener : listeners)
            {
               listener.onClose(output);
            }
         }
         catch (final IOException ex)
         {
            for (Listener listener : listeners)
            {
               listener.onException(output, ex);
            }
         }
      }
   }
}
