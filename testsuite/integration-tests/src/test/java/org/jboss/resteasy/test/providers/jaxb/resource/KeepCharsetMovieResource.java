package org.jboss.resteasy.test.providers.jaxb.resource;

import java.nio.charset.Charset;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.logging.Logger;

@Path("/")
public class KeepCharsetMovieResource {

    private static Logger logger = Logger.getLogger(KeepCharsetMovieResource.class);

    @POST
    @Path("/xml/produces")
    @Consumes("application/xml")
    @Produces("application/xml;charset=UTF-16")
    public KeepCharsetFavoriteMovieXmlRootElement xmlProduces(KeepCharsetFavoriteMovieXmlRootElement movie) {
        logger.info("server default charset: " + Charset.defaultCharset());
        logger.info("title: " + movie.getTitle());
        return movie;
    }

    @POST
    @Path("/xml/accepts")
    @Consumes("application/xml")
    public KeepCharsetFavoriteMovieXmlRootElement xmlAccepts(KeepCharsetFavoriteMovieXmlRootElement movie) {
        logger.info("server default charset: " + Charset.defaultCharset());
        logger.info("title: " + movie.getTitle());
        return movie;
    }

    @POST
    @Path("/xml/default")
    @Consumes("application/xml")
    @Produces("application/xml")
    public KeepCharsetFavoriteMovieXmlRootElement xmlDefault(KeepCharsetFavoriteMovieXmlRootElement movie) {
        logger.info("server default charset: " + Charset.defaultCharset());
        logger.info("title: " + movie.getTitle());
        return movie;
    }
}
