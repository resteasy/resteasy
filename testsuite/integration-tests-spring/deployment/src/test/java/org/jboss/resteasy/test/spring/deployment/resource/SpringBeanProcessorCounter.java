package org.jboss.resteasy.test.spring.deployment.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/count")
public class SpringBeanProcessorCounter {
   int counter;

   @POST
   public String count() {
      return Integer.toString(counter++);
   }
}
