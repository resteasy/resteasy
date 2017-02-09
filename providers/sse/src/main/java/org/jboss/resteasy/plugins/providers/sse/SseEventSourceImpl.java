package org.jboss.resteasy.plugins.providers.sse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventInput;
import javax.ws.rs.sse.SseEventSource;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.plugins.providers.sse.i18n.Messages;


public class SseEventSourceImpl implements SseEventSource
{
   public static final long RECONNECT_DEFAULT = 500;

   private enum State {
      READY, OPEN, CLOSED
   }

   private WebTarget target = null;
   private final long reconnectDelay;
   private final boolean disableKeepAlive;
   private final ScheduledExecutorService executor;
   private final AtomicReference<State> state = new AtomicReference<>(State.READY);
   private final List<Listener> unboundListeners = new CopyOnWriteArrayList<>();
   private final ConcurrentMap<String, List<Listener>> boundListeners = new ConcurrentHashMap<>();

   public static class SourceBuilder extends Builder
   {
      private WebTarget endpoint = null;
      private long reconnect = RECONNECT_DEFAULT;
      private String name = null;
      private boolean disableKeepAlive = false;

      public SourceBuilder(final WebTarget endpoint)
      {
         this.endpoint = endpoint;
      }

      public Builder named(String name)
      {
         this.name = name;
         return this;
      }

      public SseEventSource build()
      {
         return new SseEventSourceImpl(endpoint, name, reconnect, disableKeepAlive, false);
      }

      public SseEventSource open()
      {
         // why this api is required ? build can create SseEventSource and this can be invoked against SseEventSource
         final SseEventSource source = new SseEventSourceImpl(endpoint, name, reconnect, disableKeepAlive, false);
         source.open();
         return source;
      }

      @Override
      public Builder target(WebTarget endpoint)
      {
         return new SourceBuilder(endpoint);
      }

      @Override
      public Builder register(Listener listener)
      {
         //TODO: this api should be revised
         return this;
      }

      @Override
      public Builder register(Listener listener, String eventName, String... eventNames)
      {
         //TODO: this api should be revised
         return this;
      }

      @Override
      public Builder reconnectingEvery(long delay, TimeUnit unit)
      {
         reconnect = unit.toMillis(delay);
         return this;
      }
   }

   public SseEventSourceImpl(final WebTarget endpoint)
   {
      this(endpoint, true);
   }

   public SseEventSourceImpl(final WebTarget endpoint, final boolean open)
   {
      this(endpoint, null, RECONNECT_DEFAULT, true, open);
   }

   private SseEventSourceImpl(final WebTarget target, String name, long reconnectDelay, final boolean disableKeepAlive,
         final boolean open)
   {
      if (target == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.webTargetIsNotSetForEventSource());
      }
      this.target = target;
      this.reconnectDelay = reconnectDelay;
      this.disableKeepAlive = disableKeepAlive;

      if (name == null)
      {
         name = String.format("sse-event-source(%s)", target.getUri());
      }
      this.executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());

      if (open)
      {
         open();
      }
   }

   private static class DaemonThreadFactory implements ThreadFactory
   {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      DaemonThreadFactory()
      {
         SecurityManager s = System.getSecurityManager();
         group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         namePrefix = "resteasy-sse-eventsource" + poolNumber.getAndIncrement() + "-thread-";
      }

      public Thread newThread(Runnable r)
      {
         Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
         t.setDaemon(true);
         return t;
      }
   }

   @Override
   public void open()
   {
      if (!state.compareAndSet(State.READY, State.OPEN))
      {
         throw new IllegalStateException(Messages.MESSAGES.eventSourceIsNotReadyForOpen());
      }
      EventHandler handler = new EventHandler(reconnectDelay, null);
      executor.submit(handler);
      handler.awaitConnected();
   }

   public boolean isOpen()
   {
      return state.get() == State.OPEN;
   }

   @Override
   public void close()
   {
      this.close(5, TimeUnit.SECONDS);
   }

   public void register(final Listener listener)
   {
      register(listener, null);
   }

   public void register(final Listener listener, final String eventName, final String... eventNames)
   {
      if (eventName == null)
      {
         unboundListeners.add(listener);
      }
      else
      {
         addBoundListener(eventName, listener);

         if (eventNames != null)
         {
            for (String name : eventNames)
            {
               addBoundListener(name, listener);
            }
         }
      }
   }

   private void addBoundListener(final String name, final Listener listener)
   {
      List<Listener> listeners = boundListeners.putIfAbsent(name,
            new CopyOnWriteArrayList<>(Collections.singleton(listener)));
      if (listeners != null)
      {
         listeners.add(listener);
      }
   }

   @Override
   public boolean close(final long timeout, final TimeUnit unit)
   {
      if (state.getAndSet(State.CLOSED) != State.CLOSED)
      {
         executor.shutdownNow();
      }
      try
      {
         if (!executor.awaitTermination(timeout, unit))
         {
            return false;
         }
      }
      catch (InterruptedException e)
      {
         Thread.currentThread().interrupt();
         return false;
      }
      return true;
   }

   private class EventHandler implements Runnable, Listener
   {

      private final CountDownLatch connectedLatch;
      private String lastEventId;
      private long reconnectDelay;

      public EventHandler(final long reconnectDelay, final String lastEventId)
      {
         this.connectedLatch = new CountDownLatch(1);
         this.reconnectDelay = reconnectDelay;
         this.lastEventId = lastEventId;
      }

      private EventHandler(final EventHandler anotherHandler)
      {
         this.connectedLatch = anotherHandler.connectedLatch;
         this.reconnectDelay = anotherHandler.reconnectDelay;
         this.lastEventId = anotherHandler.lastEventId;
      }

      @Override
      public void run()
      {
         SseEventInput eventInput = null;
         final Invocation.Builder request = buildRequest();
         if (state.get() == State.OPEN)
         {
            eventInput = request.get(SseEventInput.class);

         }
         while (state.get() == State.OPEN)
         {
            if (eventInput.isClosed())
            {
               reconnect(reconnectDelay);
               break;
            }
            else
            {
               InboundSseEvent event = eventInput.read();
               if (event != null)
               {
                  onEvent(event);
               }
            }
         }
      }

      public void awaitConnected()
      {
         if (connectedLatch == null)
         {
            return;
         }
         try
         {
            connectedLatch.await(30, TimeUnit.SECONDS);
         }
         catch (InterruptedException ex)
         {
            Thread.currentThread().interrupt();
         }

      }

      @Override
      public void onEvent(final InboundSseEvent event)
      {
         if (event == null)
         {
            return;
         }
         if (event.getId() != null)
         {
            lastEventId = event.getId();
         }
         if (event.isReconnectDelaySet())
         {
            reconnectDelay = event.getReconnectDelay();
         }
         final String eventName = event.getName();
         if (eventName != null)
         {
            final List<Listener> eventListeners = boundListeners.get(eventName);
            if (eventListeners != null)
            {
               notify(eventListeners, event);
            }
         }
         notify(unboundListeners, event);
      }

      private Invocation.Builder buildRequest()
      {
         final Invocation.Builder request = target.request(MediaType.SERVER_SENT_EVENTS_TYPE);
         if (lastEventId != null && !lastEventId.isEmpty())
         {
            request.header(SseConstants.LAST_EVENT_ID_HEADER, lastEventId);
         }
         if (disableKeepAlive)
         {
            request.header(HttpHeaders.CONNECTION, "close");
         }
         return request;
      }

      private void notify(final Collection<Listener> listeners, final InboundSseEvent event)
      {
         if (listeners != null)
         {
            for (Listener listener : listeners)
            {
               listener.onEvent(event);
            }
         }
      }

      private void reconnect(final long delay)
      {
         if (state.get() != State.OPEN)
         {
            return;
         }

         EventHandler processor = new EventHandler(this);
         if (delay > 0)
         {
            executor.schedule(processor, delay, TimeUnit.MILLISECONDS);
         }
         else
         {
            executor.submit(processor);
         }
      }
   }
}
