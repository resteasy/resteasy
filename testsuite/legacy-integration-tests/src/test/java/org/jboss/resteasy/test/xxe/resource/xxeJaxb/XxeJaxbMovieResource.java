package org.jboss.resteasy.test.xxe.resource.xxeJaxb;

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
public class XxeJaxbMovieResource {

    private static Logger logger = Logger.getLogger(XxeJaxbMovieResource.class);

    @POST
    @Path("xmlRootElement")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(XxeJaxbFavoriteMovieXmlRootElement movie) {
        logger.info("CharSetMovieResource(xmlRootElment): title = " + movie.getTitle());
        return movie.getTitle();
    }

    @POST
    @Path("xmlType")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(XxeJaxbFavoriteMovieXmlType movie) {
        logger.info("CharSetMovieResource(xmlType): title = " + movie.getTitle());
        return movie.getTitle();
    }

    @POST
    @Path("JAXBElement")
    @Consumes("application/xml")
    public String addFavoriteMovie(JAXBElement<XxeJaxbFavoriteMovie> value) {
        logger.info("CharSetMovieResource(JAXBElement): title = " + value.getValue().getTitle());
        return value.getValue().getTitle();
    }

    @POST
    @Path("list")
    @Consumes("application/xml")
    public String addFavoriteMovie(List<XxeJaxbFavoriteMovieXmlRootElement> list) {
        String titles = "";
        Iterator<XxeJaxbFavoriteMovieXmlRootElement> it = list.iterator();
        while (it.hasNext()) {
            String title = it.next().getTitle();
            logger.info("CharSetMovieResource(list): title = " + title);
            titles += title;
        }
        return titles;
    }

    @POST
    @Path("set")
    @Consumes("application/xml")
    public String addFavoriteMovie(Set<XxeJaxbFavoriteMovieXmlRootElement> set) {
        String titles = "";
        Iterator<XxeJaxbFavoriteMovieXmlRootElement> it = set.iterator();
        while (it.hasNext()) {
            String title = it.next().getTitle();
            logger.info("CharSetMovieResource(list): title = " + title);
            titles += title;
        }
        return titles;
    }

    @POST
    @Path("array")
    @Consumes("application/xml")
    public String addFavoriteMovie(XxeJaxbFavoriteMovieXmlRootElement[] array) {
        String titles = "";
        for (int i = 0; i < array.length; i++) {
            String title = array[i].getTitle();
            logger.info("CharSetMovieResource(list): title = " + title);
            titles += title;
        }
        return titles;
    }

    @POST
    @Path("map")
    @Consumes("application/xml")
    public String addFavoriteMovie(Map<String, XxeJaxbFavoriteMovieXmlRootElement> map) {
        String titles = "";
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String title = map.get(it.next()).getTitle();
            logger.info("CharSetMovieResource(map): title = " + title);
            titles += title;
        }
        return titles;
    }
}
