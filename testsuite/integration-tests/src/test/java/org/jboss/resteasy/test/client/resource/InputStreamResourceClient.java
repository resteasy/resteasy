package org.jboss.resteasy.test.client.resource;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
