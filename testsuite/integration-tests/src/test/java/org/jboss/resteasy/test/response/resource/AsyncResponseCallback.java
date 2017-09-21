package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.container.CompletionCallback;

public class AsyncResponseCallback implements CompletionCallback {

   private static boolean called;
   private static Throwable error;

   public AsyncResponseCallback()
   {
      called = false;
      error = null;
   }
   
   @Override
   public void onComplete(Throwable throwable)
   {
      called = true;
      error = throwable;
   }
   
   public static void assertCalled(boolean withError) 
   {
      if(!called)
         throw new AssertionError("Not called");
      if(withError && error == null
            || !withError && error != null)
         throw new AssertionError("Error mismatch");
   }
}
