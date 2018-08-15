package org.jboss.resteasy.test.asynch.resource;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

@Priority(2)
@Provider
public class AsyncResponseFilter2 extends AsyncResponseFilter {

   public AsyncResponseFilter2()
   {
      super("ResponseFilter2");
   }
}
