package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.AbstractExecutionContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.UnhandledException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Servlet3AsyncHttpRequest extends HttpServletInputMessage
{
   protected HttpServletResponse response;
   protected ResteasyAsynchronousContext asynchronousContext;
   protected ScheduledExecutorService asyncScheduler; // this is to get around TCK tests that call setTimeout in a separate thread which is illegal.

   public Servlet3AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpServletResponse response, ServletContext servletContext, HttpResponse httpResponse, ResteasyHttpHeaders httpHeaders, ResteasyUriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
   {
      super(httpServletRequest, response, servletContext, httpResponse, httpHeaders, uriInfo, s, synchronousDispatcher);
      this.response = response;
      asynchronousContext = new Servlet3ExecutionContext((ServletRequest) httpServletRequest);
   }

   @Override
   public ResteasyAsynchronousContext getAsyncContext()
   {
      return asynchronousContext;
   }

   private class Servlet3ExecutionContext extends AbstractExecutionContext
   {
      protected final ServletRequest servletRequest;
      protected volatile boolean done;
      protected volatile boolean cancelled;
      protected volatile boolean wasSuspended;
      protected Servle3AsychronousResponse asynchronousResponse;

      public Servlet3ExecutionContext(ServletRequest servletRequest)
      {
         super(Servlet3AsyncHttpRequest.this.dispatcher, Servlet3AsyncHttpRequest.this, Servlet3AsyncHttpRequest.this.httpResponse);
         this.servletRequest = servletRequest;
      }

      private class Servle3AsychronousResponse extends AbstractAsynchronousResponse implements AsyncListener
      {
         private Object responseLock = new Object();
         protected WeakReference<Thread> creatingThread = new WeakReference<Thread>(Thread.currentThread());
         protected ScheduledFuture timeoutFuture; // this is to get around TCK tests that call setTimeout in a separate thread which is illegal.

         private Servle3AsychronousResponse()
         {
            super(Servlet3ExecutionContext.this.dispatcher, Servlet3ExecutionContext.this.request, Servlet3ExecutionContext.this.response);
         }

         @Override
         public boolean resume(Object entity)
         {
            synchronized (responseLock)
            {
               if (done) return false;
               if (cancelled) return false;
               AsyncContext asyncContext = getAsyncContext();
               done = true;
               return internalResume(entity, t -> asyncContext.complete());
            }

         }

         @Override
         public void complete()
         {
            synchronized (responseLock)
            {
               if (done) return;
               if (cancelled) return;
               AsyncContext asyncContext = getAsyncContext();
               done = true;
               asyncContext.complete();
            }

         }

         @Override
         public boolean resume(Throwable exc)
         {
            synchronized (responseLock)
            {
               if (done) return false;
               if (cancelled) return false;
               AsyncContext asyncContext = getAsyncContext();
               done = true;
               return internalResume(exc, t -> {
                  if(t instanceof UnhandledException)
                  {
                     internalResume(Response.status(500).build(), t2 -> asyncContext.complete());
                  }
                  else
                  {
                     asyncContext.complete();
                  }
               });
            }
         }

         @Override
         public void initialRequestThreadFinished()
         {
            // done
         }

         @Override
         public boolean setTimeout(long time, TimeUnit unit) throws IllegalStateException
         {
            //getAsyncContext().setTimeout(-1);
            synchronized (responseLock)
            {
               if (done || cancelled)
                  return false;

               // this is to get around TCK tests that call setTimeout in a separate thread which is illegal.
               if (timeoutFuture != null && !timeoutFuture.cancel(false))
               {
                  return false;
               }
               if (time <= 0) return true;
               Runnable task = new Runnable()
               {
                  @Override
                  public void run()
                  {
                     LogMessages.LOGGER.debug(Messages.MESSAGES.scheduledTimeout());
                     handleTimeout();
                  }
               };
               LogMessages.LOGGER.debug(Messages.MESSAGES.schedulingTimeout());
               timeoutFuture = asyncScheduler.schedule(task, time, unit);
            }
            return true;
         }

         @Override
         public boolean cancel()
         {
            LogMessages.LOGGER.debug(Messages.MESSAGES.cancel());
            synchronized (responseLock)
            {
               if (cancelled) {
                  LogMessages.LOGGER.debug(Messages.MESSAGES.alreadyCanceled());
                  return true;
               }
               if (done) {
                  LogMessages.LOGGER.debug(Messages.MESSAGES.alreadyDone());
                  return false;
               }
               done = true;
               cancelled = true;
               AsyncContext asyncContext = getAsyncContext();
               LogMessages.LOGGER.debug(Messages.MESSAGES.cancellingWith503());
               return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build(), t -> asyncContext.complete());
            }
         }

         @Override
         public boolean cancel(int retryAfter)
         {
            synchronized (responseLock)
            {
               if (cancelled) return true;
               if (done) return false;
               done = true;
               cancelled = true;
               AsyncContext asyncContext = getAsyncContext();
               return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build(),
                     t -> asyncContext.complete());
            }
         }

         @Override
         public boolean cancel(Date retryAfter)
         {
            synchronized (responseLock)
            {
               if (cancelled) return true;
               if (done) return false;
               done = true;
               cancelled = true;
               AsyncContext asyncContext = getAsyncContext();
               return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build(),
                     t -> asyncContext.complete());
            }
         }


         @Override
         public boolean isCancelled()
         {
            return cancelled;
         }

         @Override
         public boolean isDone()
         {
            return done;
         }

         @Override
         public boolean isSuspended()
         {
            return !done && !cancelled;
         }

         @Override
         public void onComplete(AsyncEvent asyncEvent) throws IOException
         {
            LogMessages.LOGGER.debug(Messages.MESSAGES.onComplete());
            synchronized (responseLock)
            {
               done = true;
            }
         }

         @Override
         public void onTimeout(AsyncEvent asyncEvent) throws IOException
         {
            LogMessages.LOGGER.debug(Messages.MESSAGES.onTimeout());
            synchronized (responseLock)
            {
               if (done || cancelled) return;

               response.reset();
               handleTimeout();
            }
         }

         protected void handleTimeout()
         {
            if (timeoutHandler != null)
            {
               timeoutHandler.handleTimeout(this);
            }
            if (done) return;
            resume(new ServiceUnavailableException());
         }

         @Override
         public void onError(AsyncEvent asyncEvent) throws IOException
         {
            synchronized (responseLock)
            {
               cancelled = true;
               done = true;
            }
         }

         @Override
         public void onStartAsync(AsyncEvent asyncEvent) throws IOException
         {
         }
      }

      @Override
      public ResteasyAsynchronousResponse getAsyncResponse()
      {
         return asynchronousResponse;
      }

      @Override
      public ResteasyAsynchronousResponse suspend() throws IllegalStateException
      {
         return suspend(-1);
      }

      @Override
      public ResteasyAsynchronousResponse suspend(long millis) throws IllegalStateException
      {
         return suspend(millis, TimeUnit.MILLISECONDS);
      }

      @Override
      public ResteasyAsynchronousResponse suspend(long time, TimeUnit unit) throws IllegalStateException
      {
         AsyncContext asyncContext = setupAsyncContext();
         asyncContext.setTimeout(unit.toMillis(time));
         return asynchronousResponse;
      }

      protected AsyncContext setupAsyncContext()
      {
         if (servletRequest.isAsyncStarted())
         {
            throw new IllegalStateException(Messages.MESSAGES.alreadySuspended());
         }
         asynchronousResponse = new Servle3AsychronousResponse();
         AsyncContext asyncContext = servletRequest.startAsync();
         asyncContext.addListener(asynchronousResponse);
         wasSuspended = true;
         //set time out to -1 and resteasy will take care of timeout 
         asyncContext.setTimeout(-1);
         return asyncContext;
      }


      private AsyncContext getAsyncContext()
      {
         AsyncContext asyncContext = servletRequest.getAsyncContext();
         if (asyncContext == null)
         {
            throw new IllegalStateException(Messages.MESSAGES.requestNotSuspended());
         }
         return asyncContext;
      }

      @Override
      public boolean isSuspended()
      {
         return wasSuspended;
      }

   }
}
