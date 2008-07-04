package org.jboss.resteasy.plugins.server.grizzly;

import com.sun.grizzly.ControllerStateListener;
import com.sun.grizzly.http.SelectorThread;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class GrizzlyServer implements ControllerStateListener
{
   protected int port;
   protected SelectorThread selectorThread;
   protected CountDownLatch start;
   protected CountDownLatch stop;

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   public void onStarted()
   {
   }

   public void onReady()
   {
      start.countDown();
   }

   public void onStopped()
   {
      stop.countDown();
   }

   public void onException(Throwable throwable)
   {
   }

   public void stop()
   {
      stop = new CountDownLatch(1);
      selectorThread.stopEndpoint();
      try
      {
         stop.await();
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void start() throws ServletException
   {
      start = new CountDownLatch(1);
      initSelectorThread();
      try
      {
         selectorThread.initEndpoint();
         selectorThread.getController().addStateListener(this);
         new Thread()
         {
            public void run()
            {
               try
               {
                  selectorThread.startEndpoint();
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
               catch (InstantiationException e)
               {
                  throw new RuntimeException(e);
               }

            }
         }.start();
         start.await();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   protected abstract void initSelectorThread()
           throws ServletException;
}
