package org.jboss.resteasy.test.xxe;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.GenericType;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Unit tests for RESTEASY-647.
 * 
 * Idea for test comes from Tim McCune: 
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Feb 25, 2012
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
     @Consumes({"application/*+json","application/json"})
     public String addFavoriteMovie(FavoriteMovieXmlRootElement movie)
     {
        System.out.println("MovieResource(xmlRootElment): title = " + movie.getTitle());
        return movie.getTitle();
     }
     
     @POST
     @Path("xmlType")
     @Consumes({"application/*+json","application/json"})
     public String addFavoriteMovie(FavoriteMovieXmlType movie)
     {
        System.out.println("MovieResource(xmlType): title = " + movie.getTitle());
        return movie.getTitle();
     }
     
     @POST
     @Path("JAXBElement")
     @Consumes({"application/*+json","application/json"})
     public String addFavoriteMovie(JAXBElement<FavoriteMovie> value)
     {
        System.out.println("MovieResource(JAXBElement): title = " + value.getValue().getTitle());
        return value.getValue().getTitle();
     }
     
     @POST
     @Path("list")
     @Consumes({"application/*+json","application/json"})
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
     @Consumes({"application/*+json","application/json"})
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
     @Consumes({"application/*+json","application/json"})
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
     @Consumes({"application/*+json","application/json"})
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
      FavoriteMovieXmlRootElement m = new FavoriteMovieXmlRootElement();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testXmlRootElementWithoutExpansion() throws Exception
   {
      before("false");
      FavoriteMovieXmlRootElement m = new FavoriteMovieXmlRootElement();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+json", m);
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
      FavoriteMovieXmlRootElement m = new FavoriteMovieXmlRootElement();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

//   @Test
   public void testXmlTypeDefault() throws Exception
   {
      before();
      FavoriteMovieXmlType m = new FavoriteMovieXmlType();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
//   @Test
   public void testXmlTypeWithoutExpansion() throws Exception
   {
      before("false");
      FavoriteMovieXmlType m = new FavoriteMovieXmlType();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

//   @Test
   public void testXmlTypeWithExpansion() throws Exception
   {
      before("true");
      FavoriteMovieXmlType m = new FavoriteMovieXmlType();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementDefault() throws Exception
   {
      before();
      FavoriteMovieXmlType m = new FavoriteMovieXmlType();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithoutExpansion() throws Exception
   {
      before("false");
      FavoriteMovieXmlType m = new FavoriteMovieXmlType();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithExpansion() throws Exception
   {
      before("true");
      FavoriteMovieXmlType m = new FavoriteMovieXmlType();
      m.setTitle("&xxe");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+json", m);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testListDefault() throws Exception
   {
      doListTest(null);
   }
   
   @Test
   public void testListWithoutExpansion() throws Exception
   {
      doListTest(false);
   }

   @Test
   public void testListWithExpansion() throws Exception
   {
      doListTest(true);
   }
   
   @Test
   public void testSetDefault() throws Exception
   {
      doSetTest(null);
   }
   
   @Test
   public void testSetWithoutExpansion() throws Exception
   {
      doSetTest(false);
   }

   @Test
   public void testSetWithExpansion() throws Exception
   {
      doSetTest(true);
   }
   
   @Test
   public void testArrayDefault() throws Exception
   {
      doArrayTest(null);
   }
   
   @Test
   public void testArrayWithoutExpansion() throws Exception
   {
      doArrayTest(false);
   }

   @Test
   public void testArrayWithExpansion() throws Exception
   {
      doArrayTest(true);
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
   
   void doListTest(Boolean expand) throws Exception
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
      List<FavoriteMovieXmlRootElement> list = new ArrayList<FavoriteMovieXmlRootElement>();
      FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
      m1.setTitle("&xxe");
      list.add(m1);
      FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
      m2.setTitle("Le Regle de Jeu");
      list.add(m2);
      ClientRequest request = new ClientRequest(generateURL("/list"));  
      request.body(MediaType.APPLICATION_JSON_TYPE, list, new GenericType<ArrayList<FavoriteMovieXmlRootElement>>(){});
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   void doSetTest(Boolean expand) throws Exception
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
      Set<FavoriteMovieXmlRootElement> set = new HashSet<FavoriteMovieXmlRootElement>();
      FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
      m1.setTitle("&xxe");
      set.add(m1);
      FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
      m2.setTitle("Le Regle de Jeu");
      set.add(m2);
      ClientRequest request = new ClientRequest(generateURL("/set"));  
      request.body(MediaType.APPLICATION_JSON_TYPE, set, new GenericType<Set<FavoriteMovieXmlRootElement>>(){});
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   void doArrayTest(Boolean expand) throws Exception
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
      FavoriteMovieXmlRootElement[] array = new FavoriteMovieXmlRootElement[2];
      FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
      m1.setTitle("&xxe");
      array[0] = m1;
      FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
      m2.setTitle("Le Regle de Jeu");
      array[1] = m2;
      ClientRequest request = new ClientRequest(generateURL("/array"));
      request.body(MediaType.APPLICATION_JSON_TYPE, array, new GenericType<FavoriteMovieXmlRootElement[]>(){});
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
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
      Map<String, FavoriteMovieXmlRootElement> map = new HashMap<String, FavoriteMovieXmlRootElement>();
      FavoriteMovieXmlRootElement m = new FavoriteMovieXmlRootElement();
      m.setTitle("&xxe");
      map.put("american", m);
      m = new FavoriteMovieXmlRootElement();
      m.setTitle("La Regle de Jeu");
      map.put("french", m);
      ClientRequest request = new ClientRequest(generateURL("/map"));
      request.body(MediaType.APPLICATION_JSON_TYPE, map, new GenericType<HashMap<String, FavoriteMovieXmlRootElement>>(){});
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
}
