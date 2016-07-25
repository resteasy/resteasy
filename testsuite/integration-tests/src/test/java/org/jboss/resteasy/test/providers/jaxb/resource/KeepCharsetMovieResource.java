package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.nio.charset.Charset;

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
