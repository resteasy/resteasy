package org.jboss.resteasy.reactor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
import org.jboss.resteasy.reactor.i18n.Messages;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FluxRxInvokerImpl implements FluxRxInvoker
{
   private static Object monitor = new Object();
   private ClientInvocationBuilder syncInvoker;
   private ScheduledExecutorService executorService;
   private FluxSink.OverflowStrategy overflowStrategy = FluxSink.OverflowStrategy.BUFFER;

   public FluxRxInvokerImpl(final SyncInvoker syncInvoker, final ExecutorService executorService)
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
   public Flux<?> get()
   {
      return eventSourceToObservable(getEventSource(), String.class, "GET", null, getAccept());
   }

   @Override
   public <R> Flux<?> get(Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "GET", null, getAccept());
   }

   @Override
   public <R> Flux<?> get(GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "GET", null, getAccept());
   }

   @Override
   public Flux<?> put(Entity<?> entity)
   {
      return eventSourceToObservable(getEventSource(), String.class, "PUT", entity, getAccept());
   }

   @Override
   public <R> Flux<?> put(Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "PUT", entity, getAccept());
   }

   @Override
   public <R> Flux<?> put(Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "PUT", entity, getAccept());
   }

   @Override
   public Flux<?> post(Entity<?> entity)
   {
      return eventSourceToObservable(getEventSource(), String.class, "POST", entity, getAccept());
   }

   @Override
   public <R> Flux<?> post(Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "POST", entity, getAccept());
   }

   @Override
   public <R> Flux<?> post(Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "POST", entity, getAccept());
   }

   @Override
   public Flux<?> delete()
   {
      return eventSourceToObservable(getEventSource(), String.class, "DELETE", null, getAccept());
   }

   @Override
   public <R> Flux<?> delete(Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "DELETE", null, getAccept());
   }

   @Override
   public <R> Flux<?> delete(GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "DELETE", null, getAccept());
   }

   @Override
   public Flux<?> head()
   {
      return eventSourceToObservable(getEventSource(), String.class, "HEAD", null, getAccept());
   }

   @Override
   public Flux<?> options()
   {
      return eventSourceToObservable(getEventSource(), String.class, "OPTIONS", null, getAccept());
   }

   @Override
   public <R> Flux<?> options(Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "OPTIONS", null, getAccept());
   }

   @Override
   public <R> Flux<?> options(GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "OPTIONS", null, getAccept());
   }

   @Override
   public Flux<?> trace()
   {
      return eventSourceToObservable(getEventSource(), String.class, "TRACE", null, getAccept());
   }

   @Override
   public <R> Flux<?> trace(Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "TRACE", null, getAccept());
   }

   @Override
   public <R> Flux<?> trace(GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, "TRACE", null, getAccept());
   }

   @Override
   public Flux<?> method(String name)
   {
      return eventSourceToObservable(getEventSource(), String.class, name, null, getAccept());
   }

   @Override
   public <R> Flux<?> method(String name, Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, name, null, getAccept());
   }

   @Override
   public <R> Flux<?> method(String name, GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, name, null, getAccept());
   }

   @Override
   public Flux<?> method(String name, Entity<?> entity)
   {
      return eventSourceToObservable(getEventSource(), String.class, name, entity, getAccept());
   }

   @Override
   public <R> Flux<?> method(String name, Entity<?> entity, Class<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, name, entity, getAccept());
   }

   @Override
   public <R> Flux<?> method(String name, Entity<?> entity, GenericType<R> responseType)
   {
      return eventSourceToObservable(getEventSource(), responseType, name, entity, getAccept());
   }


   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

   @Override
   public FluxSink.OverflowStrategy getOverflowStrategy()
   {
      return overflowStrategy;
   }

   @Override
   public void setOverflowStrategy(final FluxSink.OverflowStrategy overflowStrategy)
   {
      this.overflowStrategy = overflowStrategy;
   }

   private <T> Flux<T> eventSourceToObservable(SseEventSourceImpl sseEventSource, Class<T> clazz, String verb, Entity<?> entity, MediaType[] mediaTypes)
   {
      return eventSourceToFlux(
          sseEventSource,
          (InboundSseEventImpl e) -> e.readData(clazz, e.getMediaType()),
          verb,
          entity,
          mediaTypes
      );
   }

   private <T> Flux<T> eventSourceToObservable(SseEventSourceImpl sseEventSource, GenericType<T> type, String verb, Entity<?> entity, MediaType[] mediaTypes)
   {
      return eventSourceToFlux(
          sseEventSource,
          (InboundSseEventImpl e) -> e.readData(type, e.getMediaType()),
          verb,
          entity,
          mediaTypes
      );
   }

   private <T> Flux<T> eventSourceToFlux(
       final SseEventSourceImpl sseEventSource,
       final Function<InboundSseEventImpl, T> tSupplier,
       final String verb,
       final Entity<?> entity,
       final MediaType[] mediaTypes
   ) {
      final Flux<T> flux = Flux.create(emitter -> {
             sseEventSource.register(
                 (InboundSseEvent e) -> emitter.next(tSupplier.apply((InboundSseEventImpl)e)),
                 (Throwable t) -> emitter.error(t),
                 () -> emitter.complete());
             synchronized (monitor)
             {
                if (!sseEventSource.isOpen())
                {
                   sseEventSource.open(null, verb, entity, mediaTypes);
                }
             }
          },
          overflowStrategy);
      return flux;
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
