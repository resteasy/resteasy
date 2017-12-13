package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.CompletionCallback;

public class AsyncResponseCallback implements CompletionCallback {

   private static CountDownLatch latch;
   private static Throwable error;

   public AsyncResponseCallback()
   {
      latch = new CountDownLatch(1);
      error = null;
   }
   
   @Override
   public void onComplete(Throwable throwable)
   {
      latch.countDown();
      error = throwable;
   }
   
   public static void assertCalled(boolean withError) 
   {
      boolean called = false;
      try {
         called = latch.await(2, TimeUnit.SECONDS);
      } catch (Exception e) {
         //ignore
      }
      if(!called)
         throw new AssertionError("Not called");
      if(withError && error == null
            || !withError && error != null)
         throw new AssertionError("Error mismatch");
   }
}
