package org.jboss.resteasy.plugins.providers.sse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

public class SseBroadcasterImpl implements SseBroadcaster
{
   private ConcurrentLinkedQueue<SseEventSink> outputQueue = new ConcurrentLinkedQueue<>();

   private final List<BiConsumer<SseEventSink, Throwable>> onErrorConsumers = new CopyOnWriteArrayList<>();

   private final List<Consumer<SseEventSink>> closeConsumers = new CopyOnWriteArrayList<>();
   
   private final AtomicBoolean closed = new AtomicBoolean();

   public SseBroadcasterImpl()
   {
   }

   @Override
   public void close()
   {
	   if (!closed.compareAndSet(false, true))
	   {
		   return;
	   }
      //Javadoc says close the broadcaster and all subscribed {@link SseEventSink} instances.
      //is it necessay to close the subsribed SseEventSink ?
      outputQueue.forEach(evenSink -> {
         evenSink.close();
         closeConsumers.forEach(consumer -> {
            consumer.accept(evenSink);
         });
      });
      outputQueue.clear();
   }
   
   private void checkClosed()
   {
	   if (closed.get())
	   {
		   throw new IllegalStateException(Messages.MESSAGES.sseBroadcasterIsClosed());
	   }
   }

   @Override
   public void onError(BiConsumer<SseEventSink, Throwable> onError)
   {
	  checkClosed();
      onErrorConsumers.add(onError);
   }

   @Override
   public void onClose(Consumer<SseEventSink> onClose)
   {
	  checkClosed();
      closeConsumers.add(onClose);
   }

   @Override
   public void register(SseEventSink sseEventSink)
   {
	  checkClosed();
      outputQueue.add(sseEventSink);
   }

   @Override
   public CompletionStage<?> broadcast(OutboundSseEvent event)
   {
	  checkClosed();
      //return event immediately and doesn't block anything
      return CompletableFuture.runAsync(() -> {
         outputQueue.forEach(eventSink -> {
            SseEventOutputImpl outputImpl = (SseEventOutputImpl) eventSink;
            if (!outputImpl.isClosed())
            {
               outputImpl.send(event, callAllErrConsumers());
            }
            else
            {
               outputQueue.remove(eventSink);
            }
         });
      });
   }

   BiConsumer<SseEventSink, Throwable> callAllErrConsumers()
   {
      return (eventSink, err) -> {
         onErrorConsumers.forEach(consumer -> {
            consumer.accept(eventSink, err);
         });
      };

   }

}
