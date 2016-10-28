package org.jboss.resteasy.test.xxe.resource.xxeNamespace;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/")
public class MovieResource {
    private static Logger logger = Logger.getLogger(MovieResource.class);

    @POST
    @Path("xmlRootElement")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(FavoriteMovieXmlRootElement movie) {
        logger.info("CharSetMovieResource(xmlRootElment): title = " + movie.getTitle());
        return movie.getTitle();
    }

    @POST
    @Path("xmlType")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(FavoriteMovieXmlType movie) {
        logger.info("CharSetMovieResource(xmlType): title = " + movie.getTitle());
        return movie.getTitle();
    }

    @POST
    @Path("JAXBElement")
    @Consumes("application/xml")
    public String addFavoriteMovie(JAXBElement<FavoriteMovie> value) {
        logger.info("CharSetMovieResource(JAXBElement): title = " + value.getValue().getTitle());
        return value.getValue().getTitle();
    }

    @POST
    @Path("list")
    @Consumes("application/xml")
    public String addFavoriteMovie(List<FavoriteMovieXmlRootElement> list) {
        String titles = "";
        Iterator<FavoriteMovieXmlRootElement> it = list.iterator();
        while (it.hasNext()) {
            String title = it.next().getTitle();
            logger.info("CharSetMovieResource(list): title = " + title);
            titles += "/" + title;
        }
        return titles;
    }

    @POST
    @Path("set")
    @Consumes("application/xml")
    public String addFavoriteMovie(Set<FavoriteMovieXmlRootElement> set) {
        String titles = "";
        Iterator<FavoriteMovieXmlRootElement> it = set.iterator();
        while (it.hasNext()) {
            String title = it.next().getTitle();
            logger.info("CharSetMovieResource(list): title = " + title);
            titles += "/" + title;
        }
        return titles;
    }

    @POST
    @Path("array")
    @Consumes("application/xml")
    public String addFavoriteMovie(FavoriteMovieXmlRootElement[] array) {
        String titles = "";
        for (int i = 0; i < array.length; i++) {
            String title = array[i].getTitle();
            logger.info("CharSetMovieResource(list): title = " + title);
            titles += "/" + title;
        }
        return titles;
    }

    @POST
    @Path("map")
    @Consumes("application/xml")
    public String addFavoriteMovie(Map<String, FavoriteMovieXmlRootElement> map) {
        String titles = "";
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String title = map.get(it.next()).getTitle();
            logger.info("CharSetMovieResource(map): title = " + title);
            titles += "/" + title;
        }
        return titles;
    }
}
