package org.jboss.resteasy.test.async;

import javax.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

@Priority(1)
@Provider
public class AsyncRequestFilter1 extends AsyncRequestFilter {

   public AsyncRequestFilter1()
   {
      super("Filter1");
   }
}
