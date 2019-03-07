package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.container.AsyncResponse;
import java.util.concurrent.ArrayBlockingQueue;

public class JaxrsAsyncServletAsyncResponseBlockingQueue extends ArrayBlockingQueue<AsyncResponse> {

   private static final long serialVersionUID = -2445906740359075621L;

   public JaxrsAsyncServletAsyncResponseBlockingQueue(final int capacity) {
      super(capacity);
   }

}
