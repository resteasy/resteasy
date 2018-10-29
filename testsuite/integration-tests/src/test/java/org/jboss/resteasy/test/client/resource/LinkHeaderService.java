package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.LinkHeader;

import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("/linkheader")
public class LinkHeaderService {
   private static Logger logger = Logger.getLogger(LinkHeaderService.class);


   @POST
   public Response post(@HeaderParam("Link") LinkHeader linkHeader) {
      logger.info("SERVER LinkHeader: " + toString(linkHeader));
      return Response.noContent().header("Link", linkHeader).build();
   }

   @POST
   @Path("/str")
   public Response postStr(@HeaderParam("Link") String linkHeader) {
      logger.info("SERVER LINK: " + linkHeader);
      return Response.noContent().header("Link", linkHeader).build();
   }

   @HEAD
   @Path("/topic")
   public Response head(@Context UriInfo uriInfo) {
      return Response.ok()
            .header("Link", getSenderLink(uriInfo))
            .header("Link", getTopLink(uriInfo)).build();
   }

   protected String getSenderLink(UriInfo info) {
      String basePath = info.getMatchedURIs().get(0);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("sender");
      String link = "<" + builder.build().toString() + ">; rel=\"sender\"; title=\"sender\"";
      return link;
   }

   protected String getTopLink(UriInfo info) {
      String basePath = info.getMatchedURIs().get(0);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("poller");
      String link = "<" + builder.build().toString() + ">; rel=\"top-message\"; title=\"top-message\"";
      return link;
   }

   private static String toString(LinkHeader value)
   {
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      return getString(value);
   }

   private static String getString(LinkHeader value)
   {
      StringBuffer buf = new StringBuffer();
      for (Link link : value.getLinks())
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(link.toString());
      }
      return buf.toString();
   }

}
