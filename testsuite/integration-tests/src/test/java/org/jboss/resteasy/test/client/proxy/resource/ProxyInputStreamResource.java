package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public class ProxyInputStreamResource {
   @GET
   @Produces("text/plain")
   public String get() {
      return "hello world";
   }

}
