package org.jboss.resteasy.test.client.resource;

import java.io.InputStream;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/inputStream")
@Produces("text/plain")
@Consumes("text/plain")
public interface InputStreamResourceClient {

   @GET
   String getAsString();

   @GET
   Response getAsInputStream();

   @POST
   Response postInputStream(InputStream is);

   @POST
   Response postString(String s);
}
