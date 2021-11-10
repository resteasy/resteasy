package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;

@Path("/")
public interface ReaderWriterClient {
   @Path("/implicit")
   @DELETE
   Response deleteCustomer();

   @Path("/complex")
   @DELETE
   Response deleteComplex();
}
