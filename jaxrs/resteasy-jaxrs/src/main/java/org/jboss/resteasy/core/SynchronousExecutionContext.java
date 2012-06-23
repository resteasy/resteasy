package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.UnhandledException;

import javax.ws.rs.core.Response;
import java.io.IOException;
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
   protected boolean suspended;
   protected volatile boolean done;
   protected Object responseLock = new Object();

   public SynchronousExecutionContext(SynchronousDispatcher dispatcher, HttpRequest request, HttpResponse response)
   {
      super(dispatcher, request, response);
   }

   @Override
   public void resume(Object entity) throws IllegalStateException
   {
      synchronized (responseLock)
      {
         if (done) throw new IllegalStateException("Response processing is finished");
         try
         {
            super.resume(entity);
         }
         finally
         {
            syncLatch.countDown();
         }
      }
   }

   @Override
   public void resume(Exception exc) throws IllegalStateException
   {
      synchronized (responseLock)
      {
         if (done) throw new IllegalStateException("Response processing is finished");
         try
         {
            super.resume(exc);
         }
         finally
         {
            syncLatch.countDown();
         }
      }
   }

   @Override
   public void initialRequestThreadFinished()
   {
      if (!suspended) return;

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
               try
               {
                  response.sendError(503);
               }
               catch (IOException e)
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
      suspended = true;
      setSuspendTimeout(time, unit);
   }

   @Override
   public void setSuspendTimeout(long time, TimeUnit unit) throws IllegalStateException
   {
      timeout = time;
      timeoutUnit = unit;
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
      throw new NotImplementedYetException();
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


}
