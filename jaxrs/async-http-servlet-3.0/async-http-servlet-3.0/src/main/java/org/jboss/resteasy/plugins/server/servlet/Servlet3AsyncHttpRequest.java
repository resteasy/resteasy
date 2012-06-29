package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractExecutionContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

   public Servlet3AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpServletResponse response, HttpResponse httpResponse, HttpHeaders httpHeaders, UriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
   {
      super(httpServletRequest, httpResponse, httpHeaders, uriInfo, s, synchronousDispatcher);
      this.response = response;
      asynchronousContext = new Servlet3ExecutionContext((ServletRequest)httpServletRequest);
   }

   @Override
   public ResteasyAsynchronousContext getExecutionContext()
   {
      return asynchronousContext;
   }

   private class Servlet3ExecutionContext extends AbstractExecutionContext implements AsyncListener
   {
      protected final ServletRequest servletRequest;
      protected boolean done;
      protected boolean canceled;
      protected boolean suspended;

      public Servlet3ExecutionContext(ServletRequest servletRequest)
      {
         super(Servlet3AsyncHttpRequest.this.dispatcher, Servlet3AsyncHttpRequest.this, Servlet3AsyncHttpRequest.this.httpResponse);
         this.servletRequest = servletRequest;
      }

      @Override
      public void resume(Object entity) throws IllegalStateException
      {
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

      @Override
      public void resume(Throwable exc) throws IllegalStateException
      {
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

      @Override
      public void initialRequestThreadFinished()
      {
         // done
      }

      @Override
      public void suspend() throws IllegalStateException
      {
         suspend(-1);
      }

      @Override
      public void suspend(long millis) throws IllegalStateException
      {
         suspend(millis, TimeUnit.MILLISECONDS);
      }

      @Override
      public void suspend(long time, TimeUnit unit) throws IllegalStateException
      {
         if (servletRequest.isAsyncStarted())
         {
            getAsyncContext().setTimeout(unit.toMillis(time));
            return;
         }
         AsyncContext asyncContext = servletRequest.startAsync();
         asyncContext.setTimeout(unit.toMillis(time));
         asyncContext.addListener(this);
         suspended = true;
      }

      @Override
      public void setSuspendTimeout(long time, TimeUnit unit) throws IllegalStateException
      {
         AsyncContext asyncContext = getAsyncContext();
         asyncContext.setTimeout(unit.toMillis(time));
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
      public void cancel()
      {
         throw new NotImplementedYetException();
      }

      @Override
      public boolean isSuspended()
      {
         return suspended;
      }

      @Override
      public boolean isCancelled()
      {
         return canceled;
      }

      @Override
      public boolean isDone()
      {
         return done;
      }

      @Override
      public void setResponse(Object response)
      {
         throw new NotImplementedYetException();
      }

      @Override
      public Response getResponse()
      {
         throw new NotImplementedYetException();
      }

      @Override
      public void onComplete(AsyncEvent asyncEvent) throws IOException
      {
         done = true;
      }

      @Override
      public void onTimeout(AsyncEvent asyncEvent) throws IOException
      {
         canceled = true;
         response.reset();
         response.sendError(503);
         getAsyncContext().complete();
      }

      @Override
      public void onError(AsyncEvent asyncEvent) throws IOException
      {
         canceled = true;
      }

      @Override
      public void onStartAsync(AsyncEvent asyncEvent) throws IOException
      {
      }
   }
}
