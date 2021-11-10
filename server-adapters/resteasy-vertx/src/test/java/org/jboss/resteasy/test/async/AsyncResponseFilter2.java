package org.jboss.resteasy.test.async;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

@Priority(2)
@Provider
public class AsyncResponseFilter2 extends AsyncResponseFilter {

   public AsyncResponseFilter2()
   {
      super("ResponseFilter2");
   }
}
