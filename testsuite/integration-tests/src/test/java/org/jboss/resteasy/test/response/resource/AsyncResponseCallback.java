package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.CompletionCallback;
import org.jboss.logging.Logger;

public class AsyncResponseCallback implements CompletionCallback {

   private static CountDownLatch latch;
   private static Throwable error;
   protected final Logger logger = Logger.getLogger(AsyncResponseCallback.class.getName());

   public AsyncResponseCallback()
   {
      latch = new CountDownLatch(1);
      error = null;
   }

   @Override
   public void onComplete(Throwable throwable)
   {
      logger.info(this + ": entering onComplete(): " + throwable.getMessage());
      latch.countDown();
      error = throwable;
      logger.info(this + ": leaving onComplete()");
   }

   public static void assertCalled(boolean withError)
   {
      boolean called = false;
      try {
         called = latch.await(5, TimeUnit.SECONDS);
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
