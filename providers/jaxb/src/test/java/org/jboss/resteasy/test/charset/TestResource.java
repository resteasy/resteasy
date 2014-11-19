package org.jboss.resteasy.test.charset;

import static org.jboss.resteasy.test.charset.MediaTypes.APPLICATION_XML_UTF16;

import java.nio.charset.Charset;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Aug 15, 2014
 */
@Path("/")
public class TestResource
{
   @GET
   @Path("junk")
   public String junk()
   {
      System.out.println("TestResource.junk(): server is started");
      return "junk";
   }
   
   @POST
   @Path("xml/produces")
   @Consumes("application/xml")
   @Produces(APPLICATION_XML_UTF16)
   public FavoriteMovieXmlRootElement xmlProduces(FavoriteMovieXmlRootElement movie)
   {
      System.out.println("title: " + movie.getTitle());
      return movie;
   }

   @POST
   @Path("xml/accepts")
   @Consumes("application/xml")
   public FavoriteMovieXmlRootElement xmlAccepts(FavoriteMovieXmlRootElement movie)
   {
      System.out.println("title: " + movie.getTitle());
      return movie;
   }

   @POST
   @Path("xml/default")
   @Consumes("application/xml")
   @Produces("application/xml")
   public FavoriteMovieXmlRootElement xmlDefault(FavoriteMovieXmlRootElement movie)
   {
      System.out.println("title: " + movie.getTitle());
      return movie;
   }
}
