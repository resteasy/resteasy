package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.Path;

@Path("/spaces")
public class ReaderWriterSpaces {

   @Path("/with spaces")
   public ReaderWriterSub sub() {
      return new ReaderWriterSub();
   }
}
