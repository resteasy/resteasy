package org.jboss.resteasy.test.resource.basic.resource;

import org.junit.Assert;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;

@Path("/delete")
public class SpecialResourceDeleteResource {
   @DELETE
   @Consumes("text/plain")
   public void delete(String msg) {
      Assert.assertEquals("Wrong request content", "hello", msg);
   }
}
