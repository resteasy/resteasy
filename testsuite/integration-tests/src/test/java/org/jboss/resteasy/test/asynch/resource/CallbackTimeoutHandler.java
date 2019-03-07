package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import java.util.concurrent.TimeUnit;

public class CallbackTimeoutHandler implements TimeoutHandler {

   private int function = 0;

   public CallbackTimeoutHandler(final int function) {
      super();
      this.function = function;
   }

   @Override
   public void handleTimeout(AsyncResponse asyncResponse) {
      switch (function) {
         case 1:
            asyncResponse.setTimeout(200, TimeUnit.MILLISECONDS);
            break;
         case 2:
            asyncResponse.cancel();
            break;
         case 3:
            asyncResponse.resume(CallbackResource.RESUMED);
            break;
      }
   }

}
