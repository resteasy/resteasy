package org.jboss.resteasy.test.xxe;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Test;

/**b
 * Unit tests for RESTEASY-1103.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date September 1, 2014
 */
public class TestSecureProcessingFeature
{
   @XmlRootElement
   public static class Bar {
     private String _s;
     public String getS() {
       return _s;
     }
     public void setS(String s) {
       _s = s;
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
   
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static Map<String, String> EMPTY_MAP = new HashMap<String, String>();
   protected static enum MapInclusion {DEFAULT, FALSE, TRUE};
   
   protected static String bigAttributeDoc;
   
   static
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<bar ");
      for (int i = 0; i < 12000; i++)
      {
         sb.append("attr" + i + "=\"x\" ");  
      }
      sb.append("/>");
      bigAttributeDoc = sb.toString();
   }
   
   protected static String bigElementDoctype =
         "<!DOCTYPE foodocument [" +
               "<!ENTITY foo 'foo'>" +
               "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
               "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
               "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
               "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
               "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
               "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
               "]>";
   
   protected static String bigXmlRootElement = bigElementDoctype + "<favoriteMovieXmlRootElement><title>&foo5;</title></favoriteMovieXmlRootElement>";
   protected static String bigXmlType =        bigElementDoctype + "<favoriteMovie><title>&foo5;</title></favoriteMovie>";
   protected static String bigJAXBElement =    bigElementDoctype + "<favoriteMovieXmlType><title>&foo5;</title></favoriteMovieXmlType>";
   
   protected static String bigCollection = 
                                bigElementDoctype + 
                                "<collection>" +
                                   "<favoriteMovieXmlRootElement><title>&foo5;</title></favoriteMovieXmlRootElement>" +
                                   "<favoriteMovieXmlRootElement><title>&foo5;</title></favoriteMovieXmlRootElement>" +
                                "</collection>";
   
   protected static String bigMap = 
                                bigElementDoctype +
                                "<map>" +
                                  "<entry key=\"key1\">" +
                                    "<favoriteMovieXmlRootElement><title>&foo5;</title></favoriteMovieXmlRootElement>" +
                                  "</entry>" +
                                  "<entry key=\"key2\">" +
                                    "<favoriteMovieXmlRootElement><title>&foo5;</title></favoriteMovieXmlRootElement>" +
                                  "</entry>" +
                                "</map>";
   
   String bar = "<!DOCTYPE bar SYSTEM \"src/test/java/org/jboss/resteasy/test/xxe/external.dtd\"><bar><s>junk</s></bar>";

   @Path("/")
   public static class TestResource
   {
      @POST
      @Path("entityExpansion/xmlRootElement")
      @Consumes({"application/xml"})
      public String addFavoriteMovie(FavoriteMovieXmlRootElement movie)
      {
         System.out.println("TestResource(xmlRootElment): title = " + movie.getTitle().substring(0, 30));
         return movie.getTitle();
      }
      
      @POST
      @Path("entityExpansion/xmlType")
      @Consumes({"application/xml"})
      public String addFavoriteMovie(FavoriteMovieXmlType movie)
      {
         System.out.println("TestResource(xmlType): title = " + movie.getTitle().substring(0, 30));
         return movie.getTitle();
      }
      
      @POST
      @Path("entityExpansion/JAXBElement")
      @Consumes("application/xml")
      public String addFavoriteMovie(JAXBElement<FavoriteMovie> value)
      {
         System.out.println("TestResource(JAXBElement): title = " + value.getValue().getTitle().substring(0, 30));
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
            System.out.println("TestResource(collection): title = " + title.substring(0, 30));
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
            System.out.println("TestResource(map): title = " + title.substring(0, 30));
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
         return "bar";
      }
   }
   
   public static void before(Hashtable<String, String> contextParams) throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   @Test
   public void testSecurityDefaultDTDsDefaultExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.DEFAULT, MapInclusion.DEFAULT));
      doDTDFails();
      doMaxAttributesFails();
   }

   @Test
   public void testSecurityDefaultDTDsDefaultExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.DEFAULT, MapInclusion.FALSE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityDefaultDTDsDefaultExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.DEFAULT, MapInclusion.TRUE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityDefaultDTDsFalseExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.FALSE, MapInclusion.DEFAULT));
      doDTDPasses();
      doMaxEntitiesFails();
      doMaxAttributesFails();
   }
  
   @Test
   public void testSecurityDefaultDTDsFalseExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.FALSE, MapInclusion.FALSE));
      doDTDPasses();
      doMaxEntitiesFails();
      doMaxAttributesFails();
   }

   @Test
   public void testSecurityDefaultDTDsFalseExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.FALSE, MapInclusion.TRUE));
      doDTDPasses();
      doMaxEntitiesFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityDefaultDTDsTrueExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.TRUE, MapInclusion.DEFAULT));
      doDTDFails();
      doMaxAttributesFails();
   }
  
   @Test
   public void testSecurityDefaultDTDsTrueExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.TRUE, MapInclusion.FALSE));
      doDTDFails();
      doMaxAttributesFails();
   }

   @Test
   public void testSecurityDefaultDTDsTrueExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.TRUE, MapInclusion.TRUE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.DEFAULT, MapInclusion.DEFAULT));
      doDTDFails();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.DEFAULT, MapInclusion.FALSE));
      doDTDFails();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.DEFAULT, MapInclusion.TRUE));
      doDTDFails();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.FALSE, MapInclusion.DEFAULT));
      doDTDPasses();
      doMaxEntitiesPasses();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.FALSE, MapInclusion.FALSE));
      doDTDPasses();
      doMaxEntitiesPasses();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.FALSE, MapInclusion.TRUE));
      doDTDPasses();
      doMaxEntitiesPasses();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.TRUE, MapInclusion.DEFAULT));
      doDTDFails();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.TRUE, MapInclusion.FALSE));
      doDTDFails();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.TRUE, MapInclusion.TRUE));
      doDTDFails();
      doMaxAttributesPasses();
   }
   
   @Test
   public void testSecurityTrueDTDsDefaultExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.DEFAULT, MapInclusion.DEFAULT));
      doDTDFails();
      doMaxAttributesFails();
   }

   @Test
   public void testSecurityTrueDTDsDefaultExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.DEFAULT, MapInclusion.FALSE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsDefaultExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.DEFAULT, MapInclusion.TRUE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.FALSE, MapInclusion.DEFAULT));
      doDTDPasses();
      doMaxEntitiesFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.FALSE, MapInclusion.FALSE));
      doDTDPasses();
      doMaxEntitiesFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.FALSE, MapInclusion.TRUE));
      doDTDPasses();
      doMaxEntitiesFails();
      doMaxAttributesFails();
   }

   @Test
   public void testSecurityTrueDTDsTrueExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.TRUE, MapInclusion.DEFAULT));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsTrueExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.TRUE, MapInclusion.FALSE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsTrueExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.TRUE, MapInclusion.TRUE));
      doDTDFails();
      doMaxAttributesFails();
   }
   
   void doDTDFails() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/DTD"));
      request.body("application/xml", bar);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doExternalDTDFails(): result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      Assert.assertTrue(entity.contains("DOCTYPE is disallowed"));  
   }
   
   void doDTDPasses() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/DTD"));
      request.body("application/xml", bar);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doExternalDTDPasses() result: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("junk", entity);
   }
   
   void doMaxEntitiesFails() throws Exception
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/xmlRootElement"));
         request.body("application/xml", bigXmlRootElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesFails() result: " + entity);
         Assert.assertEquals(400, response.getStatus());
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/xmlType"));
         request.body("application/xml", bigXmlType);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesFails() result: " + entity);
         Assert.assertEquals(400, response.getStatus());
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/JAXBElement"));
         request.body("application/xml", bigJAXBElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesFails() result: " + entity);
         Assert.assertEquals(400, response.getStatus());
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/collection"));
         request.body("application/xml", bigCollection);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesFails() result: " + entity);
         Assert.assertEquals(400, response.getStatus());
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/map"));
         request.body("application/xml", bigMap);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesFails() result: " + entity);
         Assert.assertEquals(400, response.getStatus());
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
   }
   
   void doMaxEntitiesPasses() throws Exception
   {
      System.out.println("entering doEntityExpansionPasses()");
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/xmlRootElement"));
         request.body("application/xml", bigXmlRootElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesPasses() result: " + entity.substring(0, 30) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(100000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/xmlType"));
         request.body("application/xml", bigXmlType);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesPasses() result: " + entity.substring(0, 30) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(100000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/JAXBElement"));
         request.body("application/xml", bigJAXBElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesPasses() result: " + entity.substring(0, 30) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(100000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/collection"));
         request.body("application/xml", bigCollection);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doMaxEntitiesPasses() result: " + entity.substring(0, 30) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(200000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/entityExpansion/map"));
         request.body("application/xml", bigMap);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, 30) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(200000, countFoos(entity));
      }
   }
   
   void doMaxAttributesFails() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/maxAttributes"));
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doMaxAttributesFails() result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      Assert.assertTrue(entity.contains("has more than"));
      int pos = entity.indexOf("has more than");
      Assert.assertTrue(entity.substring(pos).contains("10,00"));
      pos = entity.indexOf("10,00", pos);
      Assert.assertTrue(entity.substring(pos).contains("attributes"));
   }
   
   void doMaxAttributesPasses() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/maxAttributes"));
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doMaxAttributesPasses() result: " + entity);
      Assert.assertEquals(200, response.getStatus()); 
      Assert.assertEquals("bar", entity);
   }
   
   private Hashtable<String, String> getParameterMap(MapInclusion securityFeature, MapInclusion disableDTDs, MapInclusion expandEntities)
   {
      Hashtable<String, String> map = new Hashtable<String, String>();
      switch (securityFeature)
      {
         case DEFAULT:
            break;
            
         case FALSE:
            map.put("resteasy.document.secure.processing.feature", "false");
            break;
            
         case TRUE:
            map.put("resteasy.document.secure.processing.feature", "true");
            break;
      }
      switch (disableDTDs)
      {
         case DEFAULT:
            break;
            
         case FALSE:
            map.put("resteasy.document.secure.disableDTDs", "false");
            break;
            
         case TRUE:
            map.put("resteasy.document.secure.disableDTDs", "true");
            break;
      }
      switch (expandEntities)
      {
         case DEFAULT:
            break;
            
         case FALSE:
            map.put("resteasy.document.expand.entity.references", "false");
            break;
            
         case TRUE:
            map.put("resteasy.document.expand.entity.references", "true");
            break;
      }
      return map;
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
