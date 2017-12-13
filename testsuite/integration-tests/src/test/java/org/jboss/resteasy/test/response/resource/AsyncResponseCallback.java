package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.CompletionCallback;

public class AsyncResponseCallback implements CompletionCallback {

//   private static volatile boolean called;
   private static CountDownLatch latch;
   private static Throwable error;
//   private final String text;
//   private static String st;

   public AsyncResponseCallback(String text)
   {
//      System.out.println("ARC: " + text);
//      this.text = text;
//      st = text;
      latch = new CountDownLatch(1);
//      called = false;
      error = null;
   }
   
   @Override
   public void onComplete(Throwable throwable)
   {
//      System.out.println("ARC.oncomplete: " + text);
      latch.countDown();
//      called = true;
      error = throwable;
   }
   
   public static void assertCalled(boolean withError) 
   {
//      System.out.println("ARC.assertCalled (s): " + st);
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
