package org.jboss.resteasy.test.response.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.CompletionCallback;

public class AsyncResponseCallback implements CompletionCallback {

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
      latches.get(s).countDown();
      if (throwable != null)
         errors.put(s, throwable);
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
