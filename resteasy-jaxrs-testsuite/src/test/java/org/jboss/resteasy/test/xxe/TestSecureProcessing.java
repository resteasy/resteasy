package org.jboss.resteasy.test.xxe;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Hashtable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Unit tests for RESTEASY-1103.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date September 1, 2014
 */
public class TestSecureProcessing
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected enum MapInclusion {DEFAULT, FALSE, TRUE};
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   protected static String bigExpansionDoc =
         "<!DOCTYPE foodocument [" +
               "<!ENTITY foo 'foo'>" +
               "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
               "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
               "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
               "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
               "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
               "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
               "]>" +
               "<element>&foo5;</element>";
   
   protected static String bigAttributeDoc;
   static
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<element ");
      for (int i = 0; i < 12000; i++)
      {
         sb.append("attr" + i + "=\"x\" ");  
      }
      sb.append(">bar</element>");
      bigAttributeDoc = sb.toString();
   }
   
   String smallDtd = "<!DOCTYPE bardocument [<!ELEMENT bar (ALL)>]><bar>bar</bar>";
   
   protected static String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
   protected static String externalEntityDoc =
         "<?xml version=\"1.0\"?>\r" +
         "<!DOCTYPE foo\r" +
         "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
         "]>\r" + 
         "<element>&xxe;</element>";
   
   public static class TestExceptionMapper implements ExceptionMapper<ReaderException>
   {
      @Override
      public Response toResponse(ReaderException exception)
      {  
         return Response.status(400).entity(exception.getMessage()).build();
      } 
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   @Path("/")
   public static class TestResource
   {
      @Consumes("application/xml")
      @POST
      @Path("test")
      public String doPost(Document doc)
      {
         Node node = doc.getDocumentElement();
         //System.out.println("name: " + node.getNodeName());
         NodeList children = doc.getDocumentElement().getChildNodes();
         node = children.item(0);
         //System.out.println("name: " + node.getNodeName());
         String text = node.getTextContent();
         int len = Math.min(text.length(), 30);
         //System.out.println("text: " + text.substring(0, len));
         return text;
      }
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   public static void before(Hashtable<String, String> contextParams) throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      deployment.getProviderFactory().register(TestExceptionMapper.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      Thread.sleep(1000);
      dispatcher = null;
      deployment = null;
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testSecurityDefaultDTDsDefaultExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.DEFAULT, MapInclusion.DEFAULT));
      doTestSkipFailsFailsSkip();
   }

   @Test
   public void testSecurityDefaultDTDsDefaultExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.DEFAULT, MapInclusion.FALSE));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityDefaultDTDsDefaultExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.DEFAULT, MapInclusion.TRUE));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityDefaultDTDsFalseExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.FALSE, MapInclusion.DEFAULT));
      doTestFailsFailsPassesFails();
   }
  
   @Test
   public void testSecurityDefaultDTDsFalseExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.FALSE, MapInclusion.FALSE));
      doTestFailsFailsPassesFails();
   }

   @Test
   public void testSecurityDefaultDTDsFalseExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.FALSE, MapInclusion.TRUE));
      doTestFailsFailsPassesPasses();
   }
   
   @Test
   public void testSecurityDefaultDTDsTrueExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.TRUE, MapInclusion.DEFAULT));
      doTestSkipFailsFailsSkip();
   }
  
   @Test
   public void testSecurityDefaultDTDsTrueExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.TRUE, MapInclusion.FALSE));
      doTestSkipFailsFailsSkip();
   }

   @Test
   public void testSecurityDefaultDTDsTrueExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.DEFAULT, MapInclusion.TRUE, MapInclusion.TRUE));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.DEFAULT, MapInclusion.DEFAULT));
      doTestSkipPassesFailsSkip();
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.DEFAULT, MapInclusion.FALSE));
      doTestSkipPassesFailsSkip();
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.DEFAULT, MapInclusion.TRUE));
      doTestSkipPassesFailsSkip();
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.FALSE, MapInclusion.DEFAULT));
      doTestPassesPassesPassesFails();
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.FALSE, MapInclusion.FALSE));
      doTestPassesPassesPassesFails();
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.FALSE, MapInclusion.TRUE));
      doTestPassesPassesPassesPasses();
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.TRUE, MapInclusion.DEFAULT));
      doTestSkipPassesFailsSkip();
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.TRUE, MapInclusion.FALSE));
      doTestSkipPassesFailsSkip();
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.FALSE, MapInclusion.TRUE, MapInclusion.TRUE));
      doTestSkipPassesFailsSkip();
   }
   
   @Test
   public void testSecurityTrueDTDsDefaultExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.DEFAULT, MapInclusion.DEFAULT));
      doTestSkipFailsFailsSkip();
   }

   @Test
   public void testSecurityTrueDTDsDefaultExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.DEFAULT, MapInclusion.FALSE));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityTrueDTDsDefaultExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.DEFAULT, MapInclusion.TRUE));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.FALSE, MapInclusion.DEFAULT));
      doTestFailsFailsPassesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.FALSE, MapInclusion.FALSE));
      doTestFailsFailsPassesFails();
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.FALSE, MapInclusion.TRUE));
      doTestFailsFailsPassesPasses();
   }

   @Test
   public void testSecurityTrueDTDsTrueExpansionDefault() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.TRUE, MapInclusion.DEFAULT));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityTrueDTDsTrueExpansionFalse() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.TRUE, MapInclusion.FALSE));
      doTestSkipFailsFailsSkip();
   }
   
   @Test
   public void testSecurityTrueDTDsTrueExpansionTrue() throws Exception
   {
      before(getParameterMap(MapInclusion.TRUE, MapInclusion.TRUE, MapInclusion.TRUE));
      doTestSkipFailsFailsSkip();
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   void doTestSkipFailsFailsSkip() throws Exception
   {
      doMaxAttributesFails();
      doDTDFails();
   }
   
   void doTestSkipPassesFailsSkip() throws Exception
   {
      doMaxAttributesPasses();
      doDTDFails();
   }

   void doTestFailsFailsPassesFails() throws Exception
   {
      doEntityExpansionFails();
      doMaxAttributesFails();
      doDTDPasses();
      doExternalEntityExpansionFails();
   }

   void doTestFailsFailsPassesPasses() throws Exception
   {
      doEntityExpansionFails();
      doMaxAttributesFails();
      doDTDPasses();
      doExternalEntityExpansionPasses();
   }

   void doTestPassesPassesPassesFails() throws Exception
   {
      doEntityExpansionPasses();
      doMaxAttributesPasses();
      doDTDPasses();
      doExternalEntityExpansionFails();
   }

   void doTestPassesPassesPassesPasses() throws Exception
   {
      doEntityExpansionPasses();
      doMaxAttributesPasses();
      doDTDPasses();
      doExternalEntityExpansionPasses();
   }
   
   void doEntityExpansionFails() throws Exception
   {
      //System.out.println("entering doEntityExpansionFails()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", bigExpansionDoc);
      ClientResponse<?> response = request.post();
      //System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      //System.out.println("doEntityExpansionFails() result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("org.xml.sax.SAXParseException"));
   }
   
   void doEntityExpansionPasses() throws Exception
   {
      //System.out.println("entering doEntityExpansionFails()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", bigExpansionDoc);
      ClientResponse<?> response = request.post();
      //System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      int len = Math.min(entity.length(), 30);
      //System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(countFoos(entity) > 64000);
   }
   
   void doMaxAttributesFails() throws Exception
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      //System.out.println("dbf.getClass(): " + dbf.getClass());
      if ("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl".equals(dbf.getClass().getName()))
      {
         //System.out.println("Testing with Red Hat version of Xerces, skipping max attributes test");
         return;
      }
      //System.out.println("entering doMaxAttributesFails()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      //System.out.println("doMaxAttributesFails() status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      //System.out.println("doMaxAttributesFails() result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("org.xml.sax.SAXParseException"));
      Assert.assertTrue(entity.contains("JAXP00010002:"));
      if ("en".equals(System.getProperty("user.language"))) {
         Assert.assertTrue(entity.contains("has more than \"10,00"));
         int pos = entity.indexOf("has more than \"10,00");
         Assert.assertTrue(entity.substring(pos).contains("attributes"));
      }
   }

   void doMaxAttributesPasses() throws Exception
   {
      //System.out.println("entering doMaxAttributesPasses()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      //System.out.println("doMaxAttributesPasses() status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      //System.out.println("doMaxAttributesPasses() result: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bar", entity);
   }
   
   void doDTDFails() throws Exception
   {
      //System.out.println("entering doDTDFails()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", smallDtd);
      ClientResponse<?> response = request.post();
      //System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      //System.out.println("doDTDFails(): result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("org.xml.sax.SAXParseException"));
      Assert.assertTrue(entity.contains("DOCTYPE"));
      Assert.assertTrue(entity.contains("http://apache.org/xml/features/disallow-doctype-decl"));
      Assert.assertTrue(entity.contains("true"));
   }
   
   void doDTDPasses() throws Exception
   {
      //System.out.println("entering doDTDPasses()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", smallDtd);
      ClientResponse<?> response = request.post();
      //System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      //System.out.println("doDTDPasses() result: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bar", entity);
   }
   
   void doExternalEntityExpansionFails() throws Exception
   {
      //System.out.println("entering doExternalEntityExpansionFails()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", externalEntityDoc);
      ClientResponse<?> response = request.post();
      //System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      //System.out.println("doExternalEntityExpansionFails() result: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("", entity);
   }
   
   void doExternalEntityExpansionPasses() throws Exception
   {
      //System.out.println("entering doExternalEntityExpansionPasses()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", externalEntityDoc);
      ClientResponse<?> response = request.post();
      //System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      int len = Math.min(entity.length(), 30);
      //System.out.println("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   private static Hashtable<String, String> getParameterMap(MapInclusion securityFeature, MapInclusion disableDTDs, MapInclusion expandEntities)
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
   
   private static int countFoos(String s)
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
