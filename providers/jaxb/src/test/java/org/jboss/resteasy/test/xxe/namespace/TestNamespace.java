package org.jboss.resteasy.test.xxe.namespace;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-996.
 *
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Dec 25, 2013
 */
public class TestNamespace extends BaseResourceTest
{

   @Path("/")
   public static class MovieResource
   {
     @POST
     @Path("xmlRootElement")
     @Consumes({"application/xml"})
     public String addFavoriteMovie(FavoriteMovieXmlRootElement movie)
     {
        System.out.println("MovieResource(xmlRootElment): title = " + movie.getTitle());
        return movie.getTitle();
     }

     @POST
     @Path("xmlType")
     @Consumes({"application/xml"})
     public String addFavoriteMovie(FavoriteMovieXmlType movie)
     {
        System.out.println("MovieResource(xmlType): title = " + movie.getTitle());
        return movie.getTitle();
     }

     @POST
     @Path("JAXBElement")
     @Consumes("application/xml")
     public String addFavoriteMovie(JAXBElement<FavoriteMovie> value)
     {
        System.out.println("MovieResource(JAXBElement): title = " + value.getValue().getTitle());
        return value.getValue().getTitle();
     }

     @POST
     @Path("list")
     @Consumes("application/xml")
     public String addFavoriteMovie(List<FavoriteMovieXmlRootElement> list)
     {
        String titles = "";
        Iterator<FavoriteMovieXmlRootElement> it = list.iterator();
        while (it.hasNext())
        {
           String title = it.next().getTitle();
           System.out.println("MovieResource(list): title = " + title);
           titles += "/" + title;
        }
        return titles;
     }

     @POST
     @Path("set")
     @Consumes("application/xml")
     public String addFavoriteMovie(Set<FavoriteMovieXmlRootElement> set)
     {
        String titles = "";
        Iterator<FavoriteMovieXmlRootElement> it = set.iterator();
        while (it.hasNext())
        {
           String title = it.next().getTitle();
           System.out.println("MovieResource(list): title = " + title);
           titles += "/" + title;
        }
        return titles;
     }

     @POST
     @Path("array")
     @Consumes("application/xml")
     public String addFavoriteMovie(FavoriteMovieXmlRootElement[] array)
     {
        String titles = "";
        for (int i = 0; i < array.length; i++)
        {
           String title = array[i].getTitle();
           System.out.println("MovieResource(list): title = " + title);
           titles += "/" + title;
        }
        return titles;
     }

     @POST
     @Path("map")
     @Consumes("application/xml")
     public String addFavoriteMovie(Map<String,FavoriteMovieXmlRootElement> map)
     {
        String titles = "";
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext())
        {
           String title = map.get(it.next()).getTitle();
           System.out.println("MovieResource(map): title = " + title);
           titles += "/" + title;
        }
        return titles;
     }
   }

   @Override
   @Before
   public void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("resteasy.document.expand.entity.references", "false");
      createContainer(initParams, contextParams);
      addPerRequestResource(MovieResource.class, FavoriteMovieXmlRootElement.class, FavoriteMovie.class, FavoriteMovieXmlType.class, ObjectFactory.class);
      addPackageInfo(getClass());
      super.before();
   }

   @Test
   public void testXmlRootElement() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      FavoriteMovieXmlRootElement movie = new FavoriteMovieXmlRootElement();
      movie.setTitle("La Règle du Jeu");
      request.body("application/xml", movie);
      ClientResponse<?> response = request.post();
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Règle du Jeu", entity);
      Assert.assertEquals(200, response.getStatus());
   }

   @Test
   public void testXmlType() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      FavoriteMovieXmlType movie = new FavoriteMovieXmlType();
      movie.setTitle("La Cage Aux Folles");
      request.body("application/xml", movie);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Cage Aux Folles", entity);
   }

   @Test
   public void testJAXBElement() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<favoriteMovieXmlType xmlns=\"http://abc.com\"><title>La Cage Aux Folles</title></favoriteMovieXmlType>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Cage Aux Folles", entity);
   }

   @Test
   public void testList() throws Exception
   {
      doCollectionTest("list");
   }

   @Test
   public void testSet() throws Exception
   {
      doCollectionTest("set");
   }

   @Test
   public void testArray() throws Exception
   {
      doCollectionTest("array");
   }

   @Test
   public void testMap() throws Exception
   {
      doMapTest();
   }

   void doCollectionTest(String path) throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/" + path));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<collection xmlns=\"http://abc.com\">" +
                   "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                   "<favoriteMovieXmlRootElement><title>La Règle du Jeu</title></favoriteMovieXmlRootElement>" +
                   "</collection>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (entity.indexOf("Cage") < entity.indexOf("Règle"))
      {
         Assert.assertEquals("/La Cage Aux Folles/La Règle du Jeu", entity);
      }
      else
      {
         Assert.assertEquals("/La Règle du Jeu/La Cage Aux Folles", entity);
      }
   }

   void doMapTest() throws Exception
   {

      ClientRequest request = new ClientRequest(generateURL("/map"));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<map xmlns=\"http://abc.com\">" +
                     "<entry key=\"new\">" +
                       "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                     "</entry>" +
                     "<entry key=\"old\">" +
                       "<favoriteMovieXmlRootElement><title>La Règle du Jeu</title></favoriteMovieXmlRootElement>" +
                     "</entry>" +
                   "</map>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (entity.indexOf("Cage") < entity.indexOf("Règle"))
      {
         Assert.assertEquals("/La Cage Aux Folles/La Règle du Jeu", entity);
      }
      else
      {
         Assert.assertEquals("/La Règle du Jeu/La Cage Aux Folles", entity);
      }
   }
}
