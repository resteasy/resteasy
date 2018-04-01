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

import javax.ws.rs.ProcessingException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
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
      PENDING, CONNECTING, OPEN, CLOSED
   }

   private final AtomicReference<State> state = new AtomicReference<>(State.PENDING);

   private final List<Consumer<InboundSseEvent>> onEventConsumers = new CopyOnWriteArrayList<>();

   private final List<Consumer<Throwable>> onErrorConsumers = new CopyOnWriteArrayList<>();

   private final List<Runnable> onCompleteConsumers = new CopyOnWriteArrayList<>();
   
   // No need to be volatile since visibility is guarantees by the fact that it will always be set before setting 
   // AtomicReference state to 'OPEN' and read only if AtomicReference state is set to 'OPEN'.
   private Response response;

   protected static class SourceBuilder extends Builder
   {
      private WebTarget target = null;

      private long reconnect = RECONNECT_DEFAULT;

      private boolean disableKeepAlive = false;

      public SourceBuilder()
      {
         //NOOP
      }

      public SseEventSource build()
      {
         return new SseEventSourceImpl(target, reconnect, disableKeepAlive, false);
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
      this(target, RECONNECT_DEFAULT, false, open);
   }

   private SseEventSourceImpl(final WebTarget target, long reconnectDelay, final boolean disableKeepAlive,
         final boolean open)
   {
      if (target == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.webTargetIsNotSetForEventSource());
      }
      this.target = target;
      this.reconnectDelay = reconnectDelay;
      this.disableKeepAlive = disableKeepAlive;

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
      if (!state.compareAndSet(State.PENDING, State.CONNECTING))
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
      State currentState = state.get();
      return currentState == State.OPEN  || currentState == State.CONNECTING;
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
      State currentState = state.getAndSet(State.CLOSED);
      if (currentState != State.CLOSED)
      {
         if(currentState == State.OPEN)
         {
            try
            {
               //close response to close connection
               //If already close it's fine since multiple invocation of close has no further effect.
               response.close();
            }
            catch (ProcessingException e)
            {
            }
         }
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
         if (state.get() != State.CONNECTING)
         {
            return;
         }
         
         Response localResponse = null;
         try
         {
            SseEventInputImpl eventInput = null;
            try
            {
               // Let's make request and get response.
               localResponse = buildRequest().buildGet().invoke();

               // if response code is not 2xx, fail the connection.
               if (localResponse.getStatusInfo().getFamily() != Family.SUCCESSFUL)
               {
                  // Throw instance of WebApplicationException based on response code.
                  ClientInvocation.handleErrorStatus(localResponse);
                  return;
               }

               // Let's try to get response content.
               try
               {
                  eventInput = localResponse.readEntity(SseEventInputImpl.class);
               }
               catch (IllegalStateException e)
               {
                  // If cannot get response content because of IllegalStateException, it's maybe because
                  // someone has invoked 'SseEventSourceImpl.close()', in this case we are fine this is not an 
                  // error let's just return.
                  // If no one has has invoked 'SseEventSourceImpl.close()' let's propagate this unexpected error.
                  if (state.get() != State.CLOSED)
                  {
                     throw e;
                  }
                  return;
               }

               // If no response content, fail the connection. 
               if (eventInput == null)
               {
                  state.set(State.CLOSED);
                  return;
               }
            }
            finally
            {
               connectedLatch.countDown();
            }
            
            // response must be set before switching to OPEN state to ensure that it will be visible
            // in close method.
            response = localResponse;
            // Let's switch state from CONNECTING to OPEN if possible and process event stream.
            if (state.compareAndSet(State.CONNECTING, State.OPEN))
            {
               long delay = reconnectDelay;
               while (!Thread.currentThread().isInterrupted() && state.get() == State.OPEN)
               {
                  if (eventInput.isClosed())
                  {
                     reconnect(State.OPEN, response, delay);
                     break;
                  }
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
                        state.set(State.CLOSED);
                        break;
                     }
                  }
                  catch (IOException e)
                  {
                     reconnect(State.OPEN, response, delay);
                     break;
                  }
               }
            }
            
         }
         catch (ServiceUnavailableException ex)
         {
            if (ex.hasRetryAfter())
            {
               Date requestTime = new Date();
               long delay = ex.getRetryTime(requestTime).getTime() - requestTime.getTime();
               reconnect(State.CONNECTING, localResponse, delay);
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
            if (localResponse != null)
            {
               try
               {
                  localResponse.close();
               }
               catch (ProcessingException e)
               {
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

      // Note: With an executor bounded to one thread, no really need to
      // close the previous response here since it will be closed in the finally
      // block of the run method before executing again. It is useful if the executor has
      // more than one thread.
      private void reconnect(State expectedState, Response previousResponse, final long delay)
      {
         if (state.compareAndSet(expectedState, State.CONNECTING))
         {
            // Let's close the previous response to be sure to release any
            // resource (pooled connections) before trying again. It is mostly 
            // useful when only the response headers may have been processed and the
            // response entity ignored (503 with rety-after for example).
            //
            // previousResponse must/will not be null when this method is called.
            previousResponse.close();
            EventHandler processor = new EventHandler(this);
            // Zero and negative delays are also allowed in schedule methods, and are treated as requests
            // for immediate execution. 
            executor.schedule(processor, delay, TimeUnit.MILLISECONDS);
         }
      }
      
   }

}
