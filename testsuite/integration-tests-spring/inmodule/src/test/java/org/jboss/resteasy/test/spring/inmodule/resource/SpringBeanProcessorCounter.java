package org.jboss.resteasy.test.spring.inmodule.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/count")
public class SpringBeanProcessorCounter {
   int counter;

   @POST
   public String count() {
      return Integer.toString(counter++);
   }
}
