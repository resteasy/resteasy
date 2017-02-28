package org.jboss.resteasy.plugins.providers.sse;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.Flow.Subscriber;
import javax.ws.rs.Flow.Subscription;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;

public class SseBroadcasterImpl implements SseBroadcaster
{
   private final Map<Subscriber<? super OutboundSseEvent>, Subscription> subscribers = new ConcurrentHashMap<>();
   private final Set<Consumer<Subscriber<? super OutboundSseEvent>>> onCloseConsumers = new CopyOnWriteArraySet<>();
   private final Set<BiConsumer<Subscriber<? super OutboundSseEvent>, Exception>> onExceptionConsumers = new CopyOnWriteArraySet<>();
   
   @Override
   public void broadcast(OutboundSseEvent event)
   {
      for (final Subscriber<? super OutboundSseEvent> subscriber : subscribers.keySet())
      {
         try
         {
            subscriber.onNext(event);
         }
         catch (final Exception ex)
         {
            subscriber.onError(ex); //TODO is this required?
            onExceptionConsumers.forEach(exceptioner -> exceptioner.accept(subscriber, ex));
         }
      }
   }

   @Override
   public void close()
   {
      for (final Subscriber<? super OutboundSseEvent> output : subscribers.keySet())
      {
         try
         {
            output.onComplete();
            for (Consumer<Subscriber<? super OutboundSseEvent>> consumer : onCloseConsumers)
            {
               consumer.accept(output);
            }
         }
         catch (final Exception ex)
         {
            output.onError(ex); //TODO is this required?
            onCloseConsumers.forEach(occ -> occ.accept(output));
         }
      }
   }

   @Override
   public void onException(BiConsumer<Subscriber<? super OutboundSseEvent>, Exception> onException)
   {
      onExceptionConsumers.add(onException);
   }

   @Override
   public void onClose(Consumer<Subscriber<? super OutboundSseEvent>> onClose)
   {
      onCloseConsumers.add(onClose);
   }

   @Override
   public void subscribe(Subscriber<? super OutboundSseEvent> subscriber)
   {
      final Subscription subscription = new Subscription()
      {
         public void request(long n)
         {
         }

         @Override
         public void cancel()
         {
         }
      };

      try
      {
         subscriber.onSubscribe(subscription);
         subscribers.put(subscriber, subscription);
      }
      catch (final Exception ex)
      {
         subscriber.onError(ex);
      }
   }
}
