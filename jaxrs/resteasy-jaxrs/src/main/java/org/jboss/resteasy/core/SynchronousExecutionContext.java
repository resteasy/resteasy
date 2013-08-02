package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.UnhandledException;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SynchronousExecutionContext extends AbstractExecutionContext
{
   protected final CountDownLatch syncLatch = new CountDownLatch(1);
   protected long timeout;
   protected TimeUnit timeoutUnit = TimeUnit.MILLISECONDS;
   protected boolean wasSuspended;
   protected volatile boolean done;
   protected Object responseLock = new Object();
   protected ResteasyAsynchronousResponse asynchronousResponse;

   public SynchronousExecutionContext(SynchronousDispatcher dispatcher, HttpRequest request, HttpResponse response)
   {
      super(dispatcher, request, response);
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
      wasSuspended = true;
      asynchronousResponse = new SynchronousAsynchronousResponse(dispatcher, request, response);
      asynchronousResponse.setTimeout(time, unit);
      return asynchronousResponse;
   }

   @Override
   public ResteasyAsynchronousResponse getAsyncResponse()
   {
      return asynchronousResponse;
   }

   @Override
   public boolean isSuspended()
   {
      return wasSuspended;
   }

   protected class SynchronousAsynchronousResponse extends AbstractAsynchronousResponse
   {
      protected boolean cancelled;

      public SynchronousAsynchronousResponse(SynchronousDispatcher dispatcher, HttpRequest request, HttpResponse response)
      {
         super(dispatcher, request, response);
      }



      @Override
      public boolean resume(Object entity)
      {
         synchronized (responseLock)
         {
            if (done) return false;
            if (cancelled) return false;
            try
            {
               return internalResume(entity);
            }
            finally
            {
               done = true;
               syncLatch.countDown();
            }
         }
      }


      @Override
      public boolean resume(Throwable exc) throws IllegalStateException
      {
         synchronized (responseLock)
         {
            if (done) return false;
            if (cancelled) return false;
            try
            {
               return internalResume(exc);
            }
            finally
            {
               done = true;
               syncLatch.countDown();
            }
         }
      }

      @Override
      public void initialRequestThreadFinished()
      {
         if (!wasSuspended) return;

         boolean result = false;
         try
         {
            result = syncLatch.await(timeout, timeoutUnit);
         }
         catch (InterruptedException e)
         {

         }
         if (result == false)
         {
            synchronized (responseLock)
            {
               if (!done)
               {
                  if (timeoutHandler != null)
                  {
                     timeoutHandler.handleTimeout(this);
                  }
                  if (!done)
                  {
                     try
                     {
                        internalResume(Response.status(503).build());
                     }
                     catch (Exception e)
                     {
                        throw new UnhandledException(e);
                     }
                     finally
                     {
                        done = true;
                     }
                  }
               }
            }
         }
      }

      @Override
      public boolean setTimeout(long time, TimeUnit unit) throws IllegalStateException
      {
         timeout = time;
         timeoutUnit = unit;
         if (done || cancelled) return false;
         return true;
      }

      @Override
      public boolean cancel()
      {
         synchronized (responseLock)
         {
            if (done || cancelled) return false;
            done = true;
            cancelled = true;
         }
         return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
      }

      @Override
      public boolean cancel(int retryAfter)
      {
         synchronized (responseLock)
         {
            if (done || cancelled) return false;
            done = true;
            cancelled = true;
         }
         return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build());
      }

      @Override
      public boolean cancel(Date retryAfter)
      {
         synchronized (responseLock)
         {
            if (done || cancelled) return false;
            done = true;
            cancelled = true;
         }
         return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build());
      }

      @Override
      public boolean isSuspended()
      {
         return !done;
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

   }


}
