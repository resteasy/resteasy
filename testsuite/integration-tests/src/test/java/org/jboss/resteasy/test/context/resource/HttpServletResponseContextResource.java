package org.jboss.resteasy.test.context.resource;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/")
public class HttpServletResponseContextResource {

   @Path("test")
   @GET
   public void writeToResponse(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      OutputStream os = response.getOutputStream();
      os.write("context".getBytes());
      return;
   }
}
