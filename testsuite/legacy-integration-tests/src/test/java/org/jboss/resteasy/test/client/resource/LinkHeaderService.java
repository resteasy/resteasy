package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.jboss.resteasy.spi.LinkHeader;

import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("/linkheader")
public class LinkHeaderService {
    private static Logger logger = Logger.getLogger(LinkHeaderService.class);


    @POST
    public Response post(@HeaderParam("Link") LinkHeader linkHeader) {
        logger.info("SERVER LinkHeader: " + new LinkHeaderDelegate().toString(linkHeader));
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

}
