package org.jboss.resteasy.test.async;

import javax.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

@Priority(2)
@Provider
public class AsyncRequestFilter2 extends AsyncRequestFilter {

   public AsyncRequestFilter2()
   {
      super("Filter2");
   }
}
