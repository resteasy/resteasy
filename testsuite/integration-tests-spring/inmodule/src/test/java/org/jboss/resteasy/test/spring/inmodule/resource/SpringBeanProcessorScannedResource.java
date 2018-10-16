package org.jboss.resteasy.test.spring.inmodule.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/scanned")
public class SpringBeanProcessorScannedResource {
   @GET
   public String callGet() {
      return "Hello";
   }
}
