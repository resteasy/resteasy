package org.jboss.resteasy.test.xxe;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-647.
 * 
 * Idea for test comes from Tim McCune: 
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
public class TestXXE
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

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
           titles += title;
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
           titles += title;
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
           titles += title;
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
           titles += title;
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

   public static void before(String expandEntityReferences) throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("resteasy.document.expand.entity.references", expandEntityReferences);
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(MovieResource.class);
   }

   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
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
   public void testXmlRootElementDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testXmlRootElementWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

   @Test
   public void testXmlRootElementWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";

      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }

   @Test
   public void testXmlTypeDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovie><title>&xxe;</title></favoriteMovie>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testXmlTypeWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovie><title>&xxe;</title></favoriteMovie>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

   @Test
   public void testXmlTypeWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovie><title>&xxe;</title></favoriteMovie>";

      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testJAXBElementDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      after();
   }
   
   @Test
   public void testListDefault() throws Exception
   {
      doCollectionTest(null, "list");
   }
   
   @Test
   public void testListWithoutExpansion() throws Exception
   {
      doCollectionTest(false, "list");
   }

   @Test
   public void testListWithExpansion() throws Exception
   {
      doCollectionTest(true, "list");
   }
   
   @Test
   public void testSetDefault() throws Exception
   {
      doCollectionTest(null, "set");
   }
   
   @Test
   public void testSetWithoutExpansion() throws Exception
   {
      doCollectionTest(false, "set");
   }

   @Test
   public void testSetWithExpansion() throws Exception
   {
      doCollectionTest(true, "set");
   }
   
   @Test
   public void testArrayDefault() throws Exception
   {
      doCollectionTest(null, "array");
   }
   
   @Test
   public void testArrayWithoutExpansion() throws Exception
   {
      doCollectionTest(false, "array");
   }

   @Test
   public void testArrayWithExpansion() throws Exception
   {
      doCollectionTest(true, "array");
   }

   @Test
   public void testMapDefault() throws Exception
   {
      doMapTest(null);
   }
   
   @Test
   public void testMapWithoutExpansion() throws Exception
   {
      doMapTest(false);
   }
   
   @Test
   public void testMapWithExpansion() throws Exception
   {
      doMapTest(true);
   }
   
   void doCollectionTest(Boolean expand, String path) throws Exception
   {
      if (expand == null)
      {
         before();
         expand = true;
      }
      else
      {
         before(Boolean.toString(expand));
      }
      ClientRequest request = new ClientRequest(generateURL("/" + path));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<collection>" +
                   "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
                   "<favoriteMovieXmlRootElement><title>Le Regle de Jeu</title></favoriteMovieXmlRootElement>" +
                   "</collection>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (expand)
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      }
      else
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      }
      after();
   }
   
   void doMapTest(Boolean expand) throws Exception
   {
      if (expand == null)
      {
         before();
         expand = true;
      }
      else
      {
         before(Boolean.toString(expand));  
      }
      ClientRequest request = new ClientRequest(generateURL("/map"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<map>" +
                      "<entry key=\"american\">" +
                         "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
                      "</entry>" +
                      "<entry key=\"french\">" +
                         "<favoriteMovieXmlRootElement><title>La Regle de Jeu</title></favoriteMovieXmlRootElement>" +
                      "</entry>" +
                   "</map>";
      
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      if (expand)
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      }
      else
      {
         Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      }
      after();
   }
}
