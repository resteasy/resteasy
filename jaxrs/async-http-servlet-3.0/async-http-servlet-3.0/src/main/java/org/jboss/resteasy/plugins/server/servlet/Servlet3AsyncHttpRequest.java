package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.AbstractExecutionContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.AsynchronousResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Servlet3AsyncHttpRequest extends HttpServletInputMessage
{
   protected HttpServletResponse response;
   protected ResteasyAsynchronousContext asynchronousContext;

   public Servlet3AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpServletResponse response, HttpResponse httpResponse, HttpHeaders httpHeaders, ResteasyUriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
   {
      super(httpServletRequest, httpResponse, httpHeaders, uriInfo, s, synchronousDispatcher);
      this.response = response;
      asynchronousContext = new Servlet3ExecutionContext((ServletRequest) httpServletRequest);
   }

   @Override
   public ResteasyAsynchronousContext getExecutionContext()
   {
      return asynchronousContext;
   }

   private class Servlet3ExecutionContext extends AbstractExecutionContext
   {
      protected final ServletRequest servletRequest;
      protected boolean done;
      protected boolean cancelled;
      protected boolean wasSuspended;
      protected Servle3AsychronousResponse asynchronousResponse;

      public Servlet3ExecutionContext(ServletRequest servletRequest)
      {
         super(Servlet3AsyncHttpRequest.this.dispatcher, Servlet3AsyncHttpRequest.this, Servlet3AsyncHttpRequest.this.httpResponse);
         this.servletRequest = servletRequest;
      }

      private class Servle3AsychronousResponse extends AbstractAsynchronousResponse implements AsyncListener
      {
         private Object responseLock = new Object();
         private Object fallback;

         private Servle3AsychronousResponse()
         {
            super(Servlet3ExecutionContext.this.dispatcher, Servlet3ExecutionContext.this.request, Servlet3ExecutionContext.this.response);
         }

         @Override
         public void resume(Object entity) throws IllegalStateException
         {
            synchronized (responseLock)
            {
               if (done) throw new IllegalStateException("Response processing is finished");
               if (cancelled) throw new IllegalStateException("Response processing is cancelled");
               AsyncContext asyncContext = getAsyncContext();
               try
               {
                  super.resume(entity);
               }
               finally
               {
                  done = true;
                  asyncContext.complete();
               }
            }

         }

         @Override
         public void resume(Throwable exc) throws IllegalStateException
         {
            synchronized (responseLock)
            {
               if (done) throw new IllegalStateException("Response processing is finished");
               if (cancelled) throw new IllegalStateException("Response processing is cancelled");
               AsyncContext asyncContext = getAsyncContext();
               try
               {
                  super.resume(exc);
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
         public void setSuspendTimeout(long time, TimeUnit unit) throws IllegalStateException
         {
            AsyncContext asyncContext = getAsyncContext();
            asyncContext.setTimeout(unit.toMillis(time));
         }

         @Override
         public void cancel()
         {
            synchronized (responseLock)
            {
               if (done || cancelled) return;
               done = true;
               cancelled = true;
               AsyncContext asyncContext = getAsyncContext();
               try
               {
                  sendResponseObject(fallback, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
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
         public void setFallbackResponse(Object response)
         {
            fallback = response;
         }

         @Override
         public Response getFallbackResponse()
         {
            throw new NotImplementedYetException();
         }

         @Override
         public boolean isSuspended()
         {
            return !done && !cancelled;
         }

         @Override
         public void onComplete(AsyncEvent asyncEvent) throws IOException
         {
            done = true;
         }

         @Override
         public void onTimeout(AsyncEvent asyncEvent) throws IOException
         {
            cancelled = true;
            done = true;
            response.reset();
            try
            {
               sendResponseObject(fallback, 503);
            }
            finally
            {
               getAsyncContext().complete();
            }
         }

         @Override
         public void onError(AsyncEvent asyncEvent) throws IOException
         {
            cancelled = true;
            done = true;
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
      public AsynchronousResponse suspend() throws IllegalStateException
      {
         return suspend(-1);
      }

      @Override
      public AsynchronousResponse suspend(long millis) throws IllegalStateException
      {
         return suspend(millis, TimeUnit.MILLISECONDS);
      }

      @Override
      public AsynchronousResponse suspend(long time, TimeUnit unit) throws IllegalStateException
      {
         if (servletRequest.isAsyncStarted())
         {
            throw new IllegalStateException("Already suspended");
         }
         asynchronousResponse = new Servle3AsychronousResponse();
         AsyncContext asyncContext = servletRequest.startAsync();
         asyncContext.setTimeout(unit.toMillis(time));
         asyncContext.addListener(asynchronousResponse);
         wasSuspended = true;
         return asynchronousResponse;
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
