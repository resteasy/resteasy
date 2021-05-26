package org.jboss.resteasy.rso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
import org.jboss.resteasy.rso.i18n.Messages;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.reactivex.BackpressureStrategy;

public class PublisherBuilderRxInvokerImpl implements PublisherBuilderRxInvoker
{
   private static Object monitor = new Object();
   private ClientInvocationBuilder syncInvoker;
   private ScheduledExecutorService executorService;

   public PublisherBuilderRxInvokerImpl(final SyncInvoker syncInvoker, final ExecutorService executorService)
   {
      if (!(syncInvoker instanceof ClientInvocationBuilder))
      {
         throw new ProcessingException(Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName()));
      }
      this.syncInvoker = (ClientInvocationBuilder) syncInvoker;
      if (executorService instanceof ScheduledExecutorService)
      {
         this.executorService = (ScheduledExecutorService) executorService;
      }
   }

   @Override
   public Publisher<?> get()
   {
      return eventSourceToPublishable(getEventSource(), String.class, "GET", null, getAccept());
   }

   @Override
   public <R> Publisher<?> get(Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "GET", null, getAccept());
   }

   @Override
   public <R> Publisher<?> get(GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "GET", null, getAccept());
   }

   @Override
   public Publisher<?> put(Entity<?> entity)
   {
      return eventSourceToPublishable(getEventSource(), String.class, "PUT", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> put(Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "PUT", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> put(Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "PUT", entity, getAccept());
   }

   @Override
   public Publisher<?> post(Entity<?> entity)
   {
      return eventSourceToPublishable(getEventSource(), String.class, "POST", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> post(Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "POST", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> post(Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "POST", entity, getAccept());
   }

   @Override
   public Publisher<?> delete()
   {
      return eventSourceToPublishable(getEventSource(), String.class, "DELETE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> delete(Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "DELETE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> delete(GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "DELETE", null, getAccept());
   }

   @Override
   public Publisher<?> head()
   {
      return eventSourceToPublishable(getEventSource(), String.class, "HEAD", null, getAccept());
   }

   @Override
   public Publisher<?> options()
   {
      return eventSourceToPublishable(getEventSource(), String.class, "OPTIONS", null, getAccept());
   }

   @Override
   public <R> Publisher<?> options(Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "OPTIONS", null, getAccept());
   }

   @Override
   public <R> Publisher<?> options(GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "OPTIONS", null, getAccept());
   }

   @Override
   public Publisher<?> trace()
   {
      return eventSourceToPublishable(getEventSource(), String.class, "TRACE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> trace(Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "TRACE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> trace(GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, "TRACE", null, getAccept());
   }

   @Override
   public Publisher<?> method(String name)
   {
      return eventSourceToPublishable(getEventSource(), String.class, name, null, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, name, null, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, name, null, getAccept());
   }

   @Override
   public Publisher<?> method(String name, Entity<?> entity)
   {
      return eventSourceToPublishable(getEventSource(), String.class, name, entity, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, name, entity, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToPublishable(getEventSource(), responseType, name, entity, getAccept());
   }


   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

//   @Override
//   public BackpressureStrategy getBackpressureStrategy() {
//      return backpressureStrategy;
//   }
//
//   @Override
//   public void setBackpressureStrategy(BackpressureStrategy backpressureStrategy) {
//      this.backpressureStrategy = backpressureStrategy;
//   }

   private class SSEPublisherParent<T> implements Publisher<T>
   {
      protected SseEventSourceImpl sseEventSource;
      protected List<Subscriber<? super T>> subscribers = new ArrayList<Subscriber<? super T>>();
      protected Consumer<Throwable> onError = (Throwable t) -> {for (Subscriber<? super T> subscriber : subscribers) {subscriber.onError(t);}};
      protected Runnable onComplete = () -> {for (Subscriber<? super T> subscriber : subscribers) {subscriber.onComplete();}};

      SSEPublisherParent(final SseEventSourceImpl sseEventSource)
      {
         this.sseEventSource = sseEventSource;
      }

      @Override
      public void subscribe(Subscriber<? super T> s)
      {
         subscribers.add(s);
      }
   }

   private class SSEPublisherType<T> extends SSEPublisherParent<T>
   {
      private GenericType<T> genericType;
      private Consumer<InboundSseEvent> onEvent =
            (InboundSseEvent e) ->
            {
               T o = e.readData(genericType, ((InboundSseEventImpl) e).getMediaType());
               for (Subscriber<? super T> subscriber : subscribers) {subscriber.onNext(o);}
            };

      SSEPublisherType(final SseEventSourceImpl sseEventSource, final GenericType<T> genericType)
      {
         super(sseEventSource);
         this.genericType = genericType;
         this.sseEventSource.register(onEvent, onError, onComplete);
      }
   }

   private class SSEPublisherClass<T> extends SSEPublisherParent<T>
   {
      private Class<T> clazz;
      private Consumer<InboundSseEvent> onEvent =
            (InboundSseEvent e) ->
            {
               T o = e.readData(clazz, ((InboundSseEventImpl) e).getMediaType());
               for (Subscriber<? super T> subscriber : subscribers) {subscriber.onNext(o);}
            };

      SSEPublisherClass(final SseEventSourceImpl sseEventSource, final Class<T> clazz)
      {
         super(sseEventSource);
         this.clazz = clazz;
         this.sseEventSource.register(onEvent, onError, onComplete);
      }
   }

   private <T> Publisher<T> eventSourceToPublishable(SseEventSourceImpl sseEventSource, Class<T> clazz, String verb, Entity<?> entity, MediaType[] mediaTypes)
   {
      synchronized (monitor)
      {
         if (!sseEventSource.isOpen())
         {
            sseEventSource.open(null, verb, entity, mediaTypes);
         }
      }
      return ReactiveStreams.fromPublisher(new SSEPublisherClass<T>(sseEventSource, clazz)).buildRs();
   }

   private <T> Publisher<T> eventSourceToPublishable(SseEventSourceImpl sseEventSource, GenericType<T> type, String verb, Entity<?> entity, MediaType[] mediaTypes)
   {
      synchronized (monitor)
      {
         if (!sseEventSource.isOpen())
         {
            sseEventSource.open(null, verb, entity, mediaTypes);
         }
      }
      return ReactiveStreams.fromPublisher(new SSEPublisherType<T>(sseEventSource, type)).buildRs();
   }

   private SseEventSourceImpl getEventSource()
   {
      SourceBuilder builder = (SourceBuilder) SseEventSource.target(syncInvoker.getTarget());
      if (executorService != null)
      {
         builder.executor(executorService);
      }
      SseEventSourceImpl sseEventSource = (SseEventSourceImpl) builder.alwaysReconnect(false).build();
      return sseEventSource;
   }

   private MediaType[] getAccept()
   {
      if (syncInvoker instanceof ClientInvocationBuilder)
      {
         ClientInvocationBuilder builder = (ClientInvocationBuilder) syncInvoker;
         List<MediaType> accept = builder.getHeaders().getAcceptableMediaTypes();
         return accept.toArray(new MediaType[accept.size()]);
      }
      else
      {
         return null;
      }
   }

   @Override
   public BackpressureStrategy getBackpressureStrategy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setBackpressureStrategy(BackpressureStrategy backpressureStrategy)
   {
      // TODO Auto-generated method stub

   }
}
