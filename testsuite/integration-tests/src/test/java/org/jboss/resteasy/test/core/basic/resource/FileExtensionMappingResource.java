package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

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
