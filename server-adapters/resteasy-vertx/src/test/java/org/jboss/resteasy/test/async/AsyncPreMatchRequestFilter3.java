package org.jboss.resteasy.test.async;

import javax.annotation.Priority;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

@PreMatching
@Priority(3)
@Provider
public class AsyncPreMatchRequestFilter3 extends AsyncRequestFilter {

   public AsyncPreMatchRequestFilter3()
   {
      super("PreMatchFilter3");
   }
}
