package org.jboss.resteasy.test.xxe.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

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
