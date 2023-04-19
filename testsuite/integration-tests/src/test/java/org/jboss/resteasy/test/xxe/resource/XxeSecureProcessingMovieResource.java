package org.jboss.resteasy.test.xxe.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/")
public class XxeSecureProcessingMovieResource {
    private static Logger logger = Logger.getLogger(XxeSecureProcessingMovieResource.class);

    @POST
    @Path("xmlRootElement")
    @Consumes({ "application/xml" })
    public String addFavoriteMovie(XxeSecureProcessingFavoriteMovieXmlRootElement movie) {
        logger.info("XxeSecureProcessingMovieResource(xmlRootElment): title = " + movie.getTitle().substring(0, 30));
        return movie.getTitle();
    }
}
