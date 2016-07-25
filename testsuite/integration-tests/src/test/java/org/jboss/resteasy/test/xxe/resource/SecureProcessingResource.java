package org.jboss.resteasy.test.xxe.resource;

import org.jboss.logging.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

@Path("/")
public class SecureProcessingResource {
    private static Logger logger = Logger.getLogger(SecureProcessingResource.class);

    @POST
    @Path("entityExpansion/xmlRootElement")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(SecureProcessingFavoriteMovieXmlRootElement movie) {
        int len = Math.min(movie.getTitle().length(), 30);
        logger.info("TestResource(xmlRootElment): title = " + movie.getTitle().substring(0, len) + "...");
        logger.info("foos: " + countFoos(movie.getTitle()));
        return movie.getTitle();
    }

    @POST
    @Path("entityExpansion/xmlType")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(SecureProcessingFavoriteMovieXmlType movie) {
        int len = Math.min(movie.getTitle().length(), 30);
        logger.info("TestResource(xmlType): title = " + movie.getTitle().substring(0, len) + "...");
        logger.info("foos: " + countFoos(movie.getTitle()));
        return movie.getTitle();
    }

    @POST
    @Path("entityExpansion/JAXBElement")
    @Consumes("application/xml")
    public String addFavoriteMovie(JAXBElement<SecureProcessingFavoriteMovie> value) {
        int len = Math.min(value.getValue().getTitle().length(), 30);
        logger.info("TestResource(JAXBElement): title = " + value.getValue().getTitle().substring(0, len) + "...");
        logger.info("foos: " + countFoos(value.getValue().getTitle()));
        return value.getValue().getTitle();
    }

    @POST
    @Path("entityExpansion/collection")
    @Consumes("application/xml")
    public String addFavoriteMovie(Set<SecureProcessingFavoriteMovieXmlRootElement> set) {
        String titles = "";
        Iterator<SecureProcessingFavoriteMovieXmlRootElement> it = set.iterator();
        while (it.hasNext()) {
            String title = it.next().getTitle();
            int len = Math.min(title.length(), 30);
            logger.info("TestResource(collection): title = " + title.substring(0, len) + "...");
            logger.info("foos: " + countFoos(title));
            titles += title;
        }
        return titles;
    }

    @POST
    @Path("entityExpansion/map")
    @Consumes("application/xml")
    public String addFavoriteMovie(Map<String, SecureProcessingFavoriteMovieXmlRootElement> map) {
        String titles = "";
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String title = map.get(it.next()).getTitle();
            int len = Math.min(title.length(), 30);
            logger.info("TestResource(map): title = " + title.substring(0, len) + "...");
            logger.info("foos: " + countFoos(title));
            titles += title;
        }
        return titles;
    }

    @POST
    @Path("DTD")
    @Consumes(MediaType.APPLICATION_XML)
    public String DTD(SecureProcessingBar secureProcessingBar) {
        logger.info("Request (bar): " + secureProcessingBar.getS());
        return secureProcessingBar.getS();
    }

    @POST
    @Path("maxAttributes")
    @Consumes(MediaType.APPLICATION_XML)
    public String maxAttributes(SecureProcessingBar secureProcessingBar) {
        logger.info("Request (bar): " + secureProcessingBar.getS());
        return secureProcessingBar.getS();
    }

    private int countFoos(String s) {
        int count = 0;
        int pos = 0;

        while (pos >= 0) {
            pos = s.indexOf("foo", pos);
            if (pos >= 0) {
                count++;
                pos += 3;
            }
        }
        return count;
    }
}
