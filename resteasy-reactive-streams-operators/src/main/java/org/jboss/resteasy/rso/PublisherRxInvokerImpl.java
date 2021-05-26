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
import org.eclipse.microprofile.reactive.streams.operators.spi.ReactiveStreamsEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
import org.jboss.resteasy.rso.i18n.Messages;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PublisherRxInvokerImpl implements PublisherRxInvoker
{
   private static Object monitor = new Object();
   private ClientInvocationBuilder syncInvoker;
   private ScheduledExecutorService executorService;
   private ReactiveStreamsEngine engine;

   public PublisherRxInvokerImpl(final SyncInvoker syncInvoker, final ExecutorService executorService)
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
      return eventSourceToPublisher(getEventSource(), String.class, "GET", null, getAccept());
   }

   @Override
   public <R> Publisher<?> get(Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "GET", null, getAccept());
   }

   @Override
   public <R> Publisher<?> get(GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "GET", null, getAccept());
   }

   @Override
   public Publisher<?> put(Entity<?> entity)
   {
      return eventSourceToPublisher(getEventSource(), String.class, "PUT", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> put(Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "PUT", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> put(Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "PUT", entity, getAccept());
   }

   @Override
   public Publisher<?> post(Entity<?> entity)
   {
      return eventSourceToPublisher(getEventSource(), String.class, "POST", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> post(Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "POST", entity, getAccept());
   }

   @Override
   public <R> Publisher<?> post(Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "POST", entity, getAccept());
   }

   @Override
   public Publisher<?> delete()
   {
      return eventSourceToPublisher(getEventSource(), String.class, "DELETE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> delete(Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "DELETE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> delete(GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "DELETE", null, getAccept());
   }

   @Override
   public Publisher<?> head()
   {
      return eventSourceToPublisher(getEventSource(), String.class, "HEAD", null, getAccept());
   }

   @Override
   public Publisher<?> options()
   {
      return eventSourceToPublisher(getEventSource(), String.class, "OPTIONS", null, getAccept());
   }

   @Override
   public <R> Publisher<?> options(Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "OPTIONS", null, getAccept());
   }

   @Override
   public <R> Publisher<?> options(GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "OPTIONS", null, getAccept());
   }

   @Override
   public Publisher<?> trace()
   {
      return eventSourceToPublisher(getEventSource(), String.class, "TRACE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> trace(Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "TRACE", null, getAccept());
   }

   @Override
   public <R> Publisher<?> trace(GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, "TRACE", null, getAccept());
   }

   @Override
   public Publisher<?> method(String name)
   {
      return eventSourceToPublisher(getEventSource(), String.class, name, null, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, name, null, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, name, null, getAccept());
   }

   @Override
   public Publisher<?> method(String name, Entity<?> entity)
   {
      return eventSourceToPublisher(getEventSource(), String.class, name, entity, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, name, entity, getAccept());
   }

   @Override
   public <R> Publisher<?> method(String name, Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToPublisher(getEventSource(), responseType, name, entity, getAccept());
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public PublisherRxInvoker reactiveStreamsEngine(ReactiveStreamsEngine engine)
   {
      this.engine = engine;
      return this;
   }

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private class SSEPublisherParent<T> implements Publisher<T>
   {
      protected SseEventSourceImpl sseEventSource;
      protected List<Subscriber<? super T>> subscribers = new ArrayList<Subscriber<? super T>>();
      protected Consumer<Throwable> onError = (Throwable t) -> {for (Subscriber<? super T> subscriber : subscribers) {subscriber.onError(t);}};
      protected Runnable onComplete = () -> {for (Subscriber<? super T> subscriber : subscribers) {subscriber.onComplete();} sseEventSource.close();};
      protected String verb;
      protected Entity<?> entity;
      protected MediaType[] mediaTypes;

      SSEPublisherParent(final SseEventSourceImpl sseEventSource, final String verb, final Entity<?> entity, final MediaType[] mediaTypes)
      {
         this.sseEventSource = sseEventSource;
         this.verb = verb;
         this.entity = entity;
         this.mediaTypes = mediaTypes;
      }

      @Override
      public void subscribe(Subscriber<? super T> s)
      {
         subscribers.add(s);
         synchronized (monitor)
         {
            if (!sseEventSource.isOpen())
            {
               sseEventSource.open(null, verb, entity, mediaTypes);
            }
         }
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

      SSEPublisherType(final SseEventSourceImpl sseEventSource, final GenericType<T> genericType, final String verb, final Entity<?> entity, final MediaType[] mediaTypes)
      {
         super(sseEventSource, verb, entity, mediaTypes);
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

      SSEPublisherClass(final SseEventSourceImpl sseEventSource, final Class<T> clazz, final String verb, final Entity<?> entity, final MediaType[] mediaTypes)
      {
         super(sseEventSource, verb, entity, mediaTypes);
         this.clazz = clazz;
         this.sseEventSource.register(onEvent, onError, onComplete);
      }
   }

   private <T> Publisher<T> eventSourceToPublisher(SseEventSourceImpl sseEventSource, Class<T> clazz, String verb, Entity<?> entity, MediaType[] mediaTypes)
   {
      SSEPublisherClass publisher = new SSEPublisherClass<T>(sseEventSource, clazz, verb, entity, mediaTypes);
      if (engine != null)
      {
         return ReactiveStreams.fromPublisher(publisher).buildRs(engine);
      }
      else
      {
         return ReactiveStreams.fromPublisher(publisher).buildRs();
      }
   }

   private <T> Publisher<T> eventSourceToPublisher(SseEventSourceImpl sseEventSource, GenericType<T> type, String verb, Entity<?> entity, MediaType[] mediaTypes)
   {
      Publisher publisher = new SSEPublisherType<T>(sseEventSource, type, verb, entity, mediaTypes);
      if (engine != null)
      {
         return ReactiveStreams.fromPublisher(publisher).buildRs(engine);
      }
      else
      {
         return ReactiveStreams.fromPublisher(publisher).buildRs();
      }
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
}
