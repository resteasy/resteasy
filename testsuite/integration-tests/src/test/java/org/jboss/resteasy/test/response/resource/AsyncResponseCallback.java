package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.container.CompletionCallback;

public class AsyncResponseCallback implements CompletionCallback {

   private static volatile boolean called;
   private static volatile Throwable error;
   private final String text;
   private static String st;

   public AsyncResponseCallback(String text)
   {
      System.out.println("ARC: " + text);
      this.text = text;
      st = text;
      called = false;
      error = null;
   }
   
   @Override
   public void onComplete(Throwable throwable)
   {
      System.out.println("ARC.oncomplete: " + text);
      called = true;
      error = throwable;
   }
   
   public static void assertCalled(boolean withError) 
   {
      System.out.println("ARC.assertCalled (s): " + st);
      if(!called)
         throw new AssertionError("Not called");
      if(withError && error == null
            || !withError && error != null)
         throw new AssertionError("Error mismatch");
   }
}
