package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@Path("test")
public class ChunkedTransferEncodingResource {

   @POST
   @Path("")
   @Produces("text/plain")
   public Response works(@Context HttpHeaders headers, String entity) {
      String header = headers.getHeaderString("Transfer-Encoding");
      if (header == null) {
         header = "null";
      }
      String headerContentLength = headers.getHeaderString("Content-Length");
      if (headerContentLength == null) {
         headerContentLength = "null";
      }
      return Response.ok(header + " " + headerContentLength).build();
   }
}
