package org.jboss.resteasy.resteasy1103;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

@Path("/")
public class TestResource
{
   @POST
   @Path("entityExpansion/xmlRootElement")
   @Consumes({"application/xml"})
   public String addFavoriteMovie(FavoriteMovieXmlRootElement movie)
   {
      int len = Math.min(movie.getTitle().length(), 30);
      System.out.println("TestResource(xmlRootElment): title = " + movie.getTitle().substring(0, len) + "...");
      System.out.println("foos: " + countFoos(movie.getTitle()));
      return movie.getTitle();
   }
   
   @POST
   @Path("entityExpansion/xmlType")
   @Consumes({"application/xml"})
   public String addFavoriteMovie(FavoriteMovieXmlType movie)
   {
      int len = Math.min(movie.getTitle().length(), 30);
      System.out.println("TestResource(xmlType): title = " + movie.getTitle().substring(0, len) + "...");
      System.out.println("foos: " + countFoos(movie.getTitle()));
      return movie.getTitle();
   }
   
   @POST
   @Path("entityExpansion/JAXBElement")
   @Consumes("application/xml")
   public String addFavoriteMovie(JAXBElement<FavoriteMovie> value)
   {
      int len = Math.min(value.getValue().getTitle().length(), 30);
      System.out.println("TestResource(JAXBElement): title = " + value.getValue().getTitle().substring(0, len) + "...");
      System.out.println("foos: " + countFoos(value.getValue().getTitle()));
      return value.getValue().getTitle();
   }
 
   @POST
   @Path("entityExpansion/collection")
   @Consumes("application/xml")
   public String addFavoriteMovie(Set<FavoriteMovieXmlRootElement> set)
   {
      String titles = "";
      Iterator<FavoriteMovieXmlRootElement> it = set.iterator();
      while (it.hasNext())
      {
         String title = it.next().getTitle();
         int len = Math.min(title.length(), 30);
         System.out.println("TestResource(collection): title = " + title.substring(0, len) + "...");
         System.out.println("foos: " + countFoos(title));
         titles += title;
      }
      return titles;
   }
   
   @POST
   @Path("entityExpansion/map")
   @Consumes("application/xml")
   public String addFavoriteMovie(Map<String,FavoriteMovieXmlRootElement> map)
   {
      String titles = "";
      Iterator<String> it = map.keySet().iterator();
      while (it.hasNext())
      {
         String title = map.get(it.next()).getTitle();
         int len = Math.min(title.length(), 30);
         System.out.println("TestResource(map): title = " + title.substring(0, len) + "...");
         System.out.println("foos: " + countFoos(title));
         titles += title;
      }
      return titles;
   }
   
   @POST
   @Path("DTD")
   @Consumes(MediaType.APPLICATION_XML)
   public String DTD(Bar bar)
   {
      System.out.println("bar: " + bar.getS());
      return bar.getS();
   }
   
   @POST
   @Path("maxAttributes")
   @Consumes(MediaType.APPLICATION_XML)
   public String maxAttributes(Bar bar)
   {
      System.out.println("bar: " + bar.getS());
      return bar.getS();
   }
   
   private int countFoos(String s)
   {
      int count = 0;
      int pos = 0;
      
      while (pos >= 0)
      {
         pos = s.indexOf("foo", pos);
         if (pos >= 0)
         {
            count++;
            pos += 3;
         }
      }
      return count;
   }
}
