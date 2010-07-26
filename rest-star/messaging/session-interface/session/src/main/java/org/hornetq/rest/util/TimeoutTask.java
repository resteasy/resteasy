package org.hornetq.rest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TimeoutTask implements Runnable
{
   protected boolean running = true;
   protected int interval = 10;
   protected Map<String, Callback> callbacks = new HashMap<String, Callback>();
   protected Thread thread;

   public TimeoutTask(int interval)
   {
      this.interval = interval;
   }

   public interface Callback
   {
      public void testTimeout(String token);
   }

   public synchronized void add(Callback callback, String token)
   {
      callbacks.put(token, callback);
   }

   public synchronized void remove(String token)
   {
      callbacks.remove(token);
   }

   public synchronized void stop()
   {
      running = false;
      thread.interrupt();
   }

   public int getInterval()
   {
      return interval;
   }

   public synchronized void setInterval(int interval)
   {
      this.interval = interval;
   }

   public void start()
   {
      thread = new Thread(this);
      thread.start();
   }

   @Override
   public void run()
   {
      while (running)
      {
         try
         {
            Thread.sleep(interval * 1000);
         }
         catch (InterruptedException e)
         {
            running = false;
            break;
         }
         synchronized (this)
         {
            List<String> list = new ArrayList<String>(callbacks.size());
            for (String token : callbacks.keySet())
            {
               list.add(token);
            }
            for (String token : list)
            {
               Callback callback = callbacks.get(token);
               callback.testTimeout(token);
            }
         }


      }
   }
}
