package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.Path;

@Path("/test")
public class ResponseExceptionMapperRuntimeExceptionResource implements ResponseExceptionMapperRuntimeExceptionResourceInterface {

   public String get() {
      throw new RuntimeException("Test error");
   }
}
