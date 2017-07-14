package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

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
