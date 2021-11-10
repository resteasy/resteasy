package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/test")
public class FileExtensionMappingResource {

   private static final Logger LOG = Logger.getLogger(FileExtensionMappingResource.class);
   @GET
   @Produces("text/plain")
   public String testPlain(@Context UriInfo uriInfo, @QueryParam("query") String query) {
      LOG.info(uriInfo.getRequestUri());
      return "plain: " + query;
   }

   @GET
   @Produces("text/html")
   public String testHtml(@Context UriInfo uriInfo, @QueryParam("query") String query) {
      LOG.info(uriInfo.getRequestUri());
      return "html: " + query;
   }
}
