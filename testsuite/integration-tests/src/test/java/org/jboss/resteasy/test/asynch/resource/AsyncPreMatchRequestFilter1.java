package org.jboss.resteasy.test.asynch.resource;

import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@PreMatching
@Priority(1)
@Provider
public class AsyncPreMatchRequestFilter1 extends AsyncRequestFilter {

   public AsyncPreMatchRequestFilter1()
   {
      super("PreMatchFilter1");
   }
}
