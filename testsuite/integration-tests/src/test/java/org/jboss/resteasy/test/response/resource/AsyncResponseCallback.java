package org.jboss.resteasy.test.response.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.container.CompletionCallback;

import org.jboss.logging.Logger;

public class AsyncResponseCallback implements CompletionCallback {

   private final Logger logger = Logger.getLogger(AsyncResponseCallback.class);
   private static Map<String, CountDownLatch> latches = new ConcurrentHashMap<String, CountDownLatch>();
   private static Map<String, Throwable> errors = new ConcurrentHashMap<String, Throwable>();
   private final String s;

   public AsyncResponseCallback(final String s)
   {
      this.s = s;
      latches.put(s, new CountDownLatch(1));
      errors.remove(s);
   }

   @Override
   public void onComplete(Throwable throwable)
   {
      logger.info("[onComplete][s = " + s + "] throwable is " + (throwable != null ? "NOT null" : "null") + " , latch count = " + latches.get(s).getCount());
      if (throwable != null) {
         Throwable old = errors.put(s, throwable);
         logger.info("[onComplete][s = " + s + "] old throwable was " + (old != null ? "NOT null" : "null"));
      }
      latches.get(s).countDown();
   }

   public static void assertCalled(String s, boolean withError)
   {
      boolean called = false;
      try {
         called = latches.get(s).await(10, TimeUnit.SECONDS);
      } catch (Exception e) {
         //ignore
      }
      if(!called)
         throw new AssertionError("Not called");
      Throwable error = errors.get(s);
      if(withError && error == null
            || !withError && error != null)
         throw new AssertionError("Error mismatch");
   }
}
