package org.jboss.resteasy.test.providers.inputstream.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

@Path("/")
public interface InputStreamWithEmptyByteArrayInterface {

   @PUT
   @Path("/upload")
   @Consumes("*/*")
   @Produces(MediaType.TEXT_PLAIN)
   Response upload(InputStream data) throws IOException;
}
