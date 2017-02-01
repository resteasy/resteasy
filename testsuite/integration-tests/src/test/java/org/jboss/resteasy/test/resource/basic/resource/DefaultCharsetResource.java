package org.jboss.resteasy.test.resource.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class DefaultCharsetResource {

   @GET
   @Produces("text/plain")
   @Path("nocharset")
   public String noCharset() {
      return "ok";
   }

   @GET
   @Produces("text/plain; charset=UTF-16")
   @Path("charset")
   public String charset() {
      return "ok";
   }
   
   @GET
   @Path("nomediatype")
   public String noMediaType() {
      return "ok";
   }
}
