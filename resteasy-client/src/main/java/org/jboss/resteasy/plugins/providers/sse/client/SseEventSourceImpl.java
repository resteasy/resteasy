package org.jboss.resteasy.plugins.providers.sse.client;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.sse.SseConstants;
import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

public class SseEventSourceImpl implements SseEventSource
{
   public static final long RECONNECT_DEFAULT = 500;

   private final WebTarget target;

   private static final long CLOSE_WAIT = 30;

   private final long reconnectDelay;

   private final boolean disableKeepAlive;
   
   private final ScheduledExecutorService executor;

   private enum State {
      PENDING, OPEN, CLOSED
   }

   private final AtomicReference<State> state = new AtomicReference<>(State.PENDING);

   private final List<Consumer<InboundSseEvent>> onEventConsumers = new CopyOnWriteArrayList<>();

   private final List<Consumer<Throwable>> onErrorConsumers = new CopyOnWriteArrayList<>();

   private final List<Runnable> onCompleteConsumers = new CopyOnWriteArrayList<>();
   
   private boolean alwaysReconnect;

   protected static class SourceBuilder extends Builder
   {
      private WebTarget target = null;

      private long reconnect = RECONNECT_DEFAULT;

      private String name = null;

      private boolean disableKeepAlive = false;

      public SourceBuilder()
      {
         //NOOP
      }

      public Builder named(String name)
      {
         this.name = name;
         return this;
      }

      public SseEventSource build()
      {
         return new SseEventSourceImpl(target, name, reconnect, disableKeepAlive, false);
      }

      @Override
      public Builder target(WebTarget target)
      {
         if (target == null)
         {
            throw new NullPointerException();
         }
         this.target = target;
         return this;
      }

      @Override
      public Builder reconnectingEvery(long delay, TimeUnit unit)
      {
         reconnect = unit.toMillis(delay);
         return this;
      }
   }

   public SseEventSourceImpl(final WebTarget target)
   {
      this(target, true);
   }

   public SseEventSourceImpl(final WebTarget target, final boolean open)
   {
      this(target, null, RECONNECT_DEFAULT, false, open);
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
      //tck requries this
      this.alwaysReconnect = true;

      if (name == null)
      {
         name = String.format("sse-event-source(%s)", target.getUri());
      }
      ScheduledExecutorService scheduledExecutor = null;
      if (target instanceof ResteasyWebTarget)
      {
         scheduledExecutor = ((ResteasyWebTarget) target).getResteasyClient().getScheduledExecutor();
      }
      this.executor = scheduledExecutor != null ? scheduledExecutor : Executors
            .newSingleThreadScheduledExecutor(new DaemonThreadFactory());
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
      open(null);
   }

   public void open(String lastEventId)
   {
      if (!state.compareAndSet(State.PENDING, State.OPEN))
      {
         throw new IllegalStateException(Messages.MESSAGES.eventSourceIsNotReadyForOpen());
      }
      EventHandler handler = new EventHandler(reconnectDelay, lastEventId);
      executor.submit(handler);
      handler.awaitConnected();
   }

   @Override
   public boolean isOpen()
   {
      return state.get() == State.OPEN;
   }

   @Override
   public void close()
   {
      this.close(CLOSE_WAIT, TimeUnit.SECONDS);
   }

   @Override
   public void register(Consumer<InboundSseEvent> onEvent)
   {
      if (onEvent == null)
      {
         throw new IllegalArgumentException();
      }
      onEventConsumers.add(onEvent);
   }

   @Override
   public void register(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError)
   {
      if (onEvent == null)
      {
         throw new IllegalArgumentException();
      }
      if (onError == null)
      {
         throw new IllegalArgumentException();
      }
      onEventConsumers.add(onEvent);
      onErrorConsumers.add(onError);
   }

   @Override
   public void register(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError, Runnable onComplete)
   {
      if (onEvent == null)
      {
         throw new IllegalArgumentException();
      }
      if (onError == null)
      {
         throw new IllegalArgumentException();
      }
      if (onComplete == null)
      {
         throw new IllegalArgumentException();
      }
      onEventConsumers.add(onEvent);
      onErrorConsumers.add(onError);
      onCompleteConsumers.add(onComplete);
   }

   @Override
   public boolean close(final long timeout, final TimeUnit unit)
   {
      if (state.getAndSet(State.CLOSED) != State.CLOSED)
      {
         ResteasyWebTarget resteasyWebTarget = (ResteasyWebTarget) target;
         //close httpEngine to close connection
         resteasyWebTarget.getResteasyClient().httpEngine().close();
         executor.shutdownNow();

         onCompleteConsumers.forEach(Runnable::run);
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
         onErrorConsumers.forEach(consumer -> {
            consumer.accept(e);
         });
         Thread.currentThread().interrupt();
         return false;
      }

      return true;
   }

   public void setAlwasyReconnect(boolean always)
   {
      this.alwaysReconnect = always;
   }
   
   private class EventHandler implements Runnable
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
         SseEventInputImpl eventInput = null;
         long delay = reconnectDelay;
         try
         {
            final Invocation.Builder request = buildRequest();
            if (state.get() == State.OPEN)
            {
               eventInput = request.get(SseEventInputImpl.class);
            }
            //if 200< response code <300 and response contentType is null, fail the connection. 
            if (eventInput == null && !alwaysReconnect)
            {
               state.set(State.CLOSED);
            }
         }
         catch (ServiceUnavailableException ex)
         {
            if (ex.hasRetryAfter())
            {
               Date requestTime = new Date();
               delay = ex.getRetryTime(requestTime).getTime() - requestTime.getTime();
            }
            else
            {
               state.set(State.CLOSED);
            }
            onErrorConsumers.forEach(consumer -> {
               consumer.accept(ex);
            });
         }
         catch (Throwable e)
         {
            onErrorConsumers.forEach(consumer -> {
               consumer.accept(e);
            });
            state.set(State.CLOSED);
         }
         finally
         {
            if (connectedLatch != null)
            {
               connectedLatch.countDown();
            }
         }
         while (!Thread.currentThread().isInterrupted() && state.get() == State.OPEN)
         {
            if (eventInput == null || eventInput.isClosed())
            {
               reconnect(delay);
               break;
            }
            else
            {
               try
               {
                  InboundSseEvent event = eventInput.read();
                  if (event != null)
                  {
                     onEvent(event);
                     if (event.isReconnectDelaySet())
                     {
                        delay = event.getReconnectDelay();
                     }
                     onEventConsumers.forEach(consumer -> {
                        consumer.accept(event);
                     });
                  }
                  else
                  {
                     //event sink closed
                     if (!alwaysReconnect)
                        break;
                  }
               }
               catch (IOException e)
               {
                  reconnect(delay);
                  break;
               }
            }
         }
      }

      public void awaitConnected()
      {
         try
         {
            connectedLatch.await(30, TimeUnit.SECONDS);
         }
         catch (InterruptedException ex)
         {
            Thread.currentThread().interrupt();
         }

      }

      private void onEvent(final InboundSseEvent event)
      {
         if (event == null)
         {
            return;
         }
         if (event.getId() != null)
         {
            lastEventId = event.getId();
         }

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
