package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.AbstractExecutionContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.logging.Logger;
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
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
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
   private final static Logger logger = Logger.getLogger(Servlet3AsyncHttpRequest.class);
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
               try
               {
                  return internalResume(entity);
               }
               finally
               {
                  done = true;
                  asyncContext.complete();
               }
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
               try
               {
                  return internalResume(exc);
               }
               catch (UnhandledException unhandled)
               {
                  return internalResume(Response.status(500).build());
               }
               finally
               {
                  done = true;
                  asyncContext.complete();
               }
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
            synchronized (responseLock)
            {
               if (done || cancelled) return false;
               Thread thread = creatingThread.get();
               if (thread != null && thread != Thread.currentThread()) {
                  // this is to get around TCK tests that call setTimeout in a separate thread which is illegal.
                  if (timeoutFuture != null  && !timeoutFuture.cancel(false)) {
                     return false;
                  }
                  Runnable task = new Runnable() {
                     @Override
                     public void run()
                     {
                        logger.debug("scheduled timeout");
                        handleTimeout();
                     }
                  };
                  logger.debug("scheduling timeout");
                  timeoutFuture = asyncScheduler.schedule(task, time, unit);
               } else {
                  AsyncContext asyncContext = getAsyncContext();
                  long l = unit.toMillis(time);
                  asyncContext.setTimeout(l);
               }

            }
            return true;
         }

         @Override
         public boolean cancel()
         {
            logger.debug("cancel()");
            synchronized (responseLock)
            {
               if (cancelled) {
                  logger.debug("-- already canceled");
                  return true;
               }
               if (done) {
                  logger.debug("-- already done");
                  return false;
               }
               done = true;
               cancelled = true;
               AsyncContext asyncContext = getAsyncContext();
               try
               {
                  logger.debug("-- cancelling with 503");
                  return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
               }
               finally
               {
                  asyncContext.complete();
               }
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
               try
               {
                  return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build());
               }
               finally
               {
                  asyncContext.complete();
               }
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
               try
               {
                  return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build());
               }
               finally
               {
                  asyncContext.complete();
               }
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
            logger.debug("onComplete");
            synchronized (responseLock)
            {
               done = true;
            }
         }

         @Override
         public void onTimeout(AsyncEvent asyncEvent) throws IOException
         {
            logger.debug("onTimeout");
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
            throw new IllegalStateException("Already suspended");
         }
         asynchronousResponse = new Servle3AsychronousResponse();
         AsyncContext asyncContext = servletRequest.startAsync();
         asyncContext.addListener(asynchronousResponse);
         wasSuspended = true;
         return asyncContext;
      }


      private AsyncContext getAsyncContext()
      {
         AsyncContext asyncContext = servletRequest.getAsyncContext();
         if (asyncContext == null)
         {
            throw new IllegalStateException("Request not suspended");
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
