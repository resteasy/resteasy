package org.jboss.resteasy.test.client.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.util.ReadFromStream;

@Path("/inputStream")
public class InputStreamResourceService {
   static String result = "hello";

   @GET
   @Produces("text/plain")
   public InputStream get()
   {
      return new ByteArrayInputStream(result.getBytes());
   }

   @POST
   @Consumes("text/plain")
   public void post(InputStream is) throws IOException
   {
      result = new String(ReadFromStream.readFromStream(1024, is));
   }

}
