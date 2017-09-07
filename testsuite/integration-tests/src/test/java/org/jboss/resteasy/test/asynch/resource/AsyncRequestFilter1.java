package org.jboss.resteasy.test.asynch.resource;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

@Priority(1)
@Provider
public class AsyncRequestFilter1 extends AsyncRequestFilter {

   public AsyncRequestFilter1()
   {
      super("Filter1");
   }
}
