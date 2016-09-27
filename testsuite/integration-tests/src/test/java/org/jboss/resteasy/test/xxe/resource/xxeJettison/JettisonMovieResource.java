package org.jboss.resteasy.test.xxe.resource.xxeJettison;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/")
public class JettisonMovieResource {

    private static final Logger log = Logger.getLogger(JettisonMovieResource.class);

    @POST
    @Path("xmlRootElement")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(FavoriteMovieXmlRootElement movie) {
        log.info("MovieResource(xmlRootElment): title = " + movie.getTitle());
        return movie.getTitle();
    }

    @POST
    @Path("xmlType")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(FavoriteMovieXmlType movie) {
        log.info("MovieResource(xmlType): title = " + movie.getTitle());
        return movie.getTitle();
    }

    @POST
    @Path("JAXBElement")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(JAXBElement<FavoriteMovie> value) {
        log.info("MovieResource(JAXBElement): title = " + value.getValue().getTitle());
        return value.getValue().getTitle();
    }

    @POST
    @Path("list")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(List<FavoriteMovieXmlRootElement> list) {
        StringBuilder titles = new StringBuilder();

        for (FavoriteMovieXmlRootElement movie : list) {
            String title = movie.getTitle();
            log.info("MovieResource(list): title = " + title);
            titles.append(title).append(", ");
        }

        titles.setLength(titles.length() - 2);
        return titles.toString();
    }

    @POST
    @Path("set")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(Set<FavoriteMovieXmlRootElement> set) {
        StringBuilder titles = new StringBuilder();

        for (FavoriteMovieXmlRootElement movie : set) {
            String title = movie.getTitle();
            log.info("MovieResource(list): title = " + title);
            titles.append(title).append(", ");
        }

        titles.setLength(titles.length() - 2);
        return titles.toString();
    }

    @POST
    @Path("array")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(FavoriteMovieXmlRootElement[] array) {
        StringBuilder titles = new StringBuilder();

        for (FavoriteMovieXmlRootElement movie : array) {
            String title = movie.getTitle();
            log.info("MovieResource(list): title = " + title);
            titles.append(title).append(", ");
        }

        titles.setLength(titles.length() - 2);
        return titles.toString();
    }

    @POST
    @Path("map")
    @Consumes({"application/*+json", "application/json"})
    public String addFavoriteMovie(Map<String, FavoriteMovieXmlRootElement> map) {
        StringBuilder titles = new StringBuilder();

        for (FavoriteMovieXmlRootElement movie : map.values()) {
            String title = movie.getTitle();
            log.info("MovieResource(map): title = " + title);
            titles.append(title).append(", ");
        }

        titles.setLength(titles.length() - 2);
        return titles.toString();
    }
}
