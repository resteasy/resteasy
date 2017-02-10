package org.jboss.resteasy.plugins.providers.sse;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.Flow.Subscriber;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventOutput;

public class SseBroadcasterImpl implements SseBroadcaster
{
   private final Set<Subscriber<? super OutboundSseEvent>> outputs = Collections.newSetFromMap(new ConcurrentHashMap<Subscriber<? super OutboundSseEvent>, Boolean>());
   private final Set<Consumer<SseEventOutput>> onCloseConsumers = Collections.newSetFromMap(new ConcurrentHashMap<Consumer<SseEventOutput>, Boolean>());
   private final Set<BiConsumer<SseEventOutput, Exception>> onExceptionConsumers = Collections.newSetFromMap(new ConcurrentHashMap<BiConsumer<SseEventOutput, Exception>, Boolean>());

   @Override
   public void broadcast(OutboundSseEvent event)
   {
      for (final Subscriber<? super OutboundSseEvent> output : outputs)
      {
         try
         {
            output.onNext(event);
         }
         catch (final Exception ex)
         {
            output.onError(ex);
            for (BiConsumer<SseEventOutput, Exception> oec : onExceptionConsumers)
            {
               oec.accept((SseEventOutput)output, ex);
            }
         }
      }
   }

   @Override
   public void close()
   {
      for (final Subscriber<? super OutboundSseEvent> output : outputs)
      {
         try
         {
            output.onComplete();
            for (Consumer<SseEventOutput> consumer : onCloseConsumers)
            {
               consumer.accept((SseEventOutput)output);
            }
         }
         catch (final Exception ex)
         {
            output.onError(ex);
            for (BiConsumer<SseEventOutput, Exception> oec : onExceptionConsumers)
            {
               oec.accept((SseEventOutput)output, ex);
            }
         }
      }
   }

   @Override
   public void onException(BiConsumer<SseEventOutput, Exception> onException)
   {
      onExceptionConsumers.add(onException);
   }

   @Override
   public void onClose(Consumer<SseEventOutput> onClose)
   {
      onCloseConsumers.add(onClose);
   }

   @Override
   public void subscribe(Subscriber<? super OutboundSseEvent> subscriber)
   {
      outputs.add(subscriber);
   }
}
