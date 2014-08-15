package org.jboss.resteasy.test.xxe.namespace;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-996.
 * 
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Dec 25, 2013
 */
public class TestNamespace
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static final MediaType APPLICATION_XML_UTF16;
   protected static final MediaType WILDCARD_UTF16;
   
   static
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("charset", "UTF-16");
      WILDCARD_UTF16 = new MediaType("*", "*", params);
      APPLICATION_XML_UTF16 = new MediaType("application", "xml", params);
   }

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
   
   @XmlRootElement
   public static class FavoriteMovieXmlRootElement {
     private String _title;
     public String getTitle() {
       return _title;
     }
     public void setTitle(String title) {
       _title = title;
     }
   }

   public static void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("resteasy.document.expand.entity.references", "false");
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(MovieResource.class);
   }
   
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testXmlRootElement() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      FavoriteMovieXmlRootElement movie = new FavoriteMovieXmlRootElement();
      movie.setTitle("La Règle du Jeu");
      request.body("application/xml", movie);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Règle du Jeu", entity);
      after();
   }
   
   @Test
   public void testXmlRootElementUtf16() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      FavoriteMovieXmlRootElement movie = new FavoriteMovieXmlRootElement();
      movie.setTitle("La Règle du Jeu");
      JAXBContext ctx = JAXBContext.newInstance(FavoriteMovieXmlRootElement.class);
      StringWriter writer = new StringWriter();
      Marshaller marshaller = ctx.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");
      marshaller.marshal(movie, writer);
      System.out.println("XmlRootElement string: " + writer.getBuffer().toString());
      request.body(APPLICATION_XML_UTF16, movie);
      request.accept(WILDCARD_UTF16);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Règle du Jeu", entity);
      after();
   }
   
   @Test
   public void testXmlType() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      FavoriteMovieXmlType movie = new FavoriteMovieXmlType();
      movie.setTitle("La Cage Aux Folles");
      request.body("application/xml", movie);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Cage Aux Folles", entity);
      after();
   }
   
   @Test
   public void testXmlTypeUtf16() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      FavoriteMovieXmlType movie = new FavoriteMovieXmlType();
      movie.setTitle("La Cage Aux Folles");
      request.body(APPLICATION_XML_UTF16, movie);
      request.accept(WILDCARD_UTF16);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Cage Aux Folles", entity);
      after();
   }
   
   @Test
   public void testJAXBElement() throws Exception
   {
      before(); 
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
      after();
   }
   
   @Test
   public void testJAXBElementUtf16() throws Exception
   {
      before(); 
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<favoriteMovieXmlType xmlns=\"http://abc.com\"><title>La Cage Aux Folles</title></favoriteMovieXmlType>";
      System.out.println(str);
      request.body(APPLICATION_XML_UTF16, str);
      request.accept(WILDCARD_UTF16);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("La Cage Aux Folles", entity);
      after();
   }
   
   @Test
   public void testList() throws Exception
   {
      doCollectionTest("list");
   }
   
   @Test
   public void testListUtf16() throws Exception
   {
      doCollectionTestUtf16("list");
   }
   
   @Test
   public void testSet() throws Exception
   {
      doCollectionTest("set");
   }
   
   @Test
   public void testSetUtf16() throws Exception
   {
      doCollectionTestUtf16("set");
   }
   
   @Test
   public void testArray() throws Exception
   {
      doCollectionTest("array");
   }
   
   @Test
   public void testArrayUtf16() throws Exception
   {
      doCollectionTestUtf16("array");
   }
   
   @Test
   public void testMap() throws Exception
   {
      doMapTest();
   }
   
   @Test
   public void testMapUtf16() throws Exception
   {
      doMapTestUtf16();
   }
   
   void testArrayObject() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("array"));
      FavoriteMovieXmlType[] fmxts = new FavoriteMovieXmlType[2];
      fmxts[0] = new FavoriteMovieXmlType();
      fmxts[0].setTitle("La Cage Aux Folles");
      fmxts[1] = new FavoriteMovieXmlType();
      fmxts[1].setTitle("La Règle du Jeu");
      request.body("application/xml", fmxts);
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
      after();
   }
   
   void testArrayObjectUtf16() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("array"));
      FavoriteMovieXmlType[] fmxts = new FavoriteMovieXmlType[2];
      fmxts[0] = new FavoriteMovieXmlType();
      fmxts[0].setTitle("La Cage Aux Folles");
      fmxts[1] = new FavoriteMovieXmlType();
      fmxts[1].setTitle("La Règle du Jeu");
      request.body(APPLICATION_XML_UTF16, fmxts);
      request.accept(WILDCARD_UTF16);
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
      after();
   }
   
   void doCollectionTest(String path) throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/" + path));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<collection xmlns=\"http://abc.com\">" +
                   "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                   "<favoriteMovieXmlRootElement><title>La RÃ¨gle du Jeu</title></favoriteMovieXmlRootElement>" +
                   "</collection>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (entity.indexOf("Cage") < entity.indexOf("RÃ¨gle"))
      {
         Assert.assertEquals("/La Cage Aux Folles/La RÃ¨gle du Jeu", entity);
      }
      else
      {
         Assert.assertEquals("/La RÃ¨gle du Jeu/La Cage Aux Folles", entity); 
      }
      after();
   }
   
   void doCollectionTestUtf16(String path) throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/" + path));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<collection xmlns=\"http://abc.com\">" +
                   "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                   "<favoriteMovieXmlRootElement><title>La Règle du Jeu</title></favoriteMovieXmlRootElement>" +
                   "</collection>";
      request.body(APPLICATION_XML_UTF16, str);
      request.accept(WILDCARD_UTF16);
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
      after();
   }
   
   void doMapTest() throws Exception
   {
      before();
      
      ClientRequest request = new ClientRequest(generateURL("/map"));
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<map xmlns=\"http://abc.com\">" +
                     "<entry key=\"new\">" +
                       "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                     "</entry>" +
                     "<entry key=\"old\">" +
                       "<favoriteMovieXmlRootElement><title>La RÃ¨gle du Jeu</title></favoriteMovieXmlRootElement>" +
                     "</entry>" +
                   "</map>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (entity.indexOf("Cage") < entity.indexOf("RÃ¨gle"))
      {
         Assert.assertEquals("/La Cage Aux Folles/La RÃ¨gle du Jeu", entity);
      }
      else
      {
         Assert.assertEquals("/La RÃ¨gle du Jeu/La Cage Aux Folles", entity); 
      }
      after();
   }
   
   void doMapTestUtf16() throws Exception
   {
      before();
      
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
      request.body(APPLICATION_XML_UTF16, str);
      request.accept(WILDCARD_UTF16);
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
      after();
   }
}
