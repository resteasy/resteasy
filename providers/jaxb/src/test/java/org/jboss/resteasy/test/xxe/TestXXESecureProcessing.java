package org.jboss.resteasy.test.xxe;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Hashtable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-869.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date August 16, 2013
 */
public class TestXXESecureProcessing
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   String doctype =
         "<!DOCTYPE foodocument [" +
               "<!ENTITY foo 'foo'>" +
               "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
               "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
               "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
               "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
               "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
               "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
               "<!ENTITY foo7 '&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;'>" +
               "<!ENTITY foo8 '&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;'>" +
               "<!ENTITY foo9 '&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;'>" +
               "]>";

   String small = doctype + "<favoriteMovieXmlRootElement><title>&foo4;</title></favoriteMovieXmlRootElement>";
   String big   = doctype + "<favoriteMovieXmlRootElement><title>&foo5;</title></favoriteMovieXmlRootElement>";

   
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
   public void testXmlRootElementDefaultSmall() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/xml", small);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity.substring(0, 30));
      System.out.println("foos: " + countFoos(entity));
      Assert.assertEquals(10000, countFoos(entity));
      after();
   }
   
   @Test
   public void testXmlRootElementDefaultBig() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/xml", big);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      after();
   }
   
   @Test
   public void testXmlRootElementWithoutExternalExpansionSmall() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/xml", small);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity.substring(0, 30));
      System.out.println("foos: " + countFoos(entity));
      Assert.assertEquals(10000, countFoos(entity));
      after();
   }
   
   @Test
   public void testXmlRootElementWithoutExternalExpansionBig() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/xml", big);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      after();
   }

   @Test
   public void testXmlRootElementWithExternalExpansionSmall() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/xml", small);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity.substring(0, 30));
      System.out.println("foos: " + countFoos(entity));
      Assert.assertEquals(10000, countFoos(entity));
      after();
   }
   
   @Test
   public void testXmlRootElementWithExternalExpansionBig() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/xml", big);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      after();
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