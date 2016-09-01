package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.jboss.resteasy.test.providers.jaxb.CharSetRE1066Test.APPLICATION_XML_UTF16;

@Path("/")
public class CharSetRE1066Resource
{

   private final Logger log = Logger.getLogger(CharSetRE1066Resource.class.getName());

   @GET
   @Path("junk")
   public String junk()
   {
      return "junk";
   }
   
   @POST
   @Path("xml/produces")
   @Consumes("application/xml")
   @Produces(APPLICATION_XML_UTF16)
   public CharSetFavoriteMovieXmlRootElement xmlProduces(CharSetFavoriteMovieXmlRootElement movie)
   {
      log.info("title: " + movie.getTitle());
      return movie;
   }

   @POST
   @Path("xml/accepts")
   @Consumes("application/xml")
   public CharSetFavoriteMovieXmlRootElement xmlAccepts(CharSetFavoriteMovieXmlRootElement movie)
   {
      log.info("title: " + movie.getTitle());
      return movie;
   }

   @POST
   @Path("xml/default")
   @Consumes("application/xml")
   @Produces("application/xml")
   public CharSetFavoriteMovieXmlRootElement xmlDefault(CharSetFavoriteMovieXmlRootElement movie)
   {
      log.info("title: " + movie.getTitle());
      return movie;
   }
}
