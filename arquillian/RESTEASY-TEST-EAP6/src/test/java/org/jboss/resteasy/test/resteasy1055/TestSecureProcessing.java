package org.jboss.resteasy.test.resteasy1055;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1055.Bar;
import org.jboss.resteasy.resteasy1055.FavoriteMovie;
import org.jboss.resteasy.resteasy1055.FavoriteMovieXmlRootElement;
import org.jboss.resteasy.resteasy1055.FavoriteMovieXmlType;
import org.jboss.resteasy.resteasy1055.ObjectFactory;
import org.jboss.resteasy.resteasy1055.TestApplication;
import org.jboss.resteasy.resteasy1055.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for RESTEASY-1055.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date September 1, 2014
 */
@RunWith(Arquillian.class)
public class TestSecureProcessing
{
   protected static Map<String, String> EMPTY_MAP = new HashMap<String, String>();
   protected static enum MapInclusion {DEFAULT, FALSE, TRUE};
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   @Deployment(name="ddd", order=1)
   public static Archive<?> createTestArchive_ddd()
   {
      return createTestArchive("ddd", "default_default_default");
   }

   @Deployment(name="ddf", order=2)
   public static Archive<?> createTestArchive_ddf()
   {
      return createTestArchive("ddf", "default_default_false");
   }

   @Deployment(name="ddt", order=3)
   public static Archive<?> createTestArchive_ddt()
   {
      return createTestArchive("ddt", "default_default_true");
   }

   @Deployment(name="dfd", order=4)
   public static Archive<?> createTestArchive_dfd()
   {
      return createTestArchive("dfd", "default_false_default");
   }
   
   @Deployment(name="dff", order=5)
   public static Archive<?> createTestArchive_dff()
   {
      return createTestArchive("dff", "default_false_false");
   }

   @Deployment(name="dft", order=6)
   public static Archive<?> createTestArchive_dft()
   {
      return createTestArchive("dft", "default_false_true");
   }
   
   @Deployment(name="dtd", order=7)
   public static Archive<?> createTestArchive_dtd()
   {
      return createTestArchive("dtd", "default_true_default");
   }
   
   @Deployment(name="dtf", order=8)
   public static Archive<?> createTestArchive_dtf()
   {
      return createTestArchive("dtf", "default_true_false");
   }

   @Deployment(name="dtt", order=9)
   public static Archive<?> createTestArchive_dtt()
   {
      return createTestArchive("dtt", "default_true_true");
   }

   @Deployment(name="fdd", order=10)
   public static Archive<?> createTestArchive_fdd()
   {
      return createTestArchive("fdd", "false_default_default");
   }

   @Deployment(name="fdf", order=11)
   public static Archive<?> createTestArchive_fdf()
   {
      return createTestArchive("fdf", "false_default_false");
   }

   @Deployment(name="fdt", order=12)
   public static Archive<?> createTestArchive_fdt()
   {
      return createTestArchive("fdt", "false_default_true");
   }
   
   @Deployment(name="ffd", order=13)
   public static Archive<?> createTestArchive_ffd()
   {
      return createTestArchive("ffd", "false_false_default");
   }

   @Deployment(name="fff", order=14)
   public static Archive<?> createTestArchive_fff()
   {
      return createTestArchive("fff", "false_false_false");
   }

   @Deployment(name="fft", order=15)
   public static Archive<?> createTestArchive_fft()
   {
      return createTestArchive("fft", "false_false_true");
   }
   /**/
   @Deployment(name="ftd", order=16)
   public static Archive<?> createTestArchive_ftd()
   {
      return createTestArchive("ftd", "false_true_default");
   }

   @Deployment(name="ftf", order=17)
   public static Archive<?> createTestArchive_ftf()
   {
      return createTestArchive("ftf", "false_true_false");
   }

   @Deployment(name="ftt", order=18)
   public static Archive<?> createTestArchive_ftt()
   {
      return createTestArchive("ftt", "false_true_true");
   }
   
   @Deployment(name="tdd", order=19)
   public static Archive<?> createTestArchive_tdd()
   {
      return createTestArchive("tdd", "true_default_default");
   }

   @Deployment(name="tdf", order=20)
   public static Archive<?> createTestArchive_tdf()
   {
      return createTestArchive("tdf", "true_default_false");
   }

   @Deployment(name="tdt", order=21)
   public static Archive<?> createTestArchive_tdt()
   {
      return createTestArchive("tdt", "true_default_true");
   }
   
   @Deployment(name="tfd", order=22)
   public static Archive<?> createTestArchive_tfd()
   {
      return createTestArchive("tfd", "true_false_default");
   }

   @Deployment(name="tff", order=23)
   public static Archive<?> createTestArchive_tff()
   {
      return createTestArchive("tff", "true_false_false");
   }

   @Deployment(name="tft", order=24)
   public static Archive<?> createTestArchive_tft()
   {
      return createTestArchive("tft", "true_false_true");
   }
   
   @Deployment(name="ttd", order=25)
   public static Archive<?> createTestArchive_ttd()
   {
      return createTestArchive("ttd", "true_true_default");
   }

   @Deployment(name="ttf", order=26)
   public static Archive<?> createTestArchive_ttf()
   {
      return createTestArchive("ttf", "true_true_false");
   }

   @Deployment(name="ttt", order=27)
   public static Archive<?> createTestArchive_ttt()
   {
      return createTestArchive("ttt", "true_true_true");
   }
   
   static Archive<?> createTestArchive(String warExt, String webXmlExt)
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1055-" + warExt + ".war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(Bar.class, FavoriteMovie.class, FavoriteMovieXmlRootElement.class)
            .addClasses(FavoriteMovieXmlType.class, ObjectFactory.class)
            .addAsWebInfResource("1055/external.dtd", "external.dtd")
            .addAsWebInfResource("1055/web_" + webXmlExt + ".xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   String bigElementDoctype =
         "<!DOCTYPE foodocument [" +
               "<!ENTITY foo 'foo'>" +
               "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
               "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
               "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
               "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
               "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
               "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
               "]>";
   
   String bigXmlRootElement = bigElementDoctype + "<favoriteMovieXmlRootElement><title>&foo6;</title></favoriteMovieXmlRootElement>";
   String bigXmlType =        bigElementDoctype + "<favoriteMovie><title>&foo6;</title></favoriteMovie>";
   String bigJAXBElement =    bigElementDoctype + "<favoriteMovieXmlType><title>&foo6;</title></favoriteMovieXmlType>";
   
   String bigCollection =     bigElementDoctype + 
                                "<collection>" +
                                   "<favoriteMovieXmlRootElement><title>&foo6;</title></favoriteMovieXmlRootElement>" +
                                   "<favoriteMovieXmlRootElement><title>&foo6;</title></favoriteMovieXmlRootElement>" +
                                "</collection>";
   
   String bigMap =            bigElementDoctype +
                                "<map>" +
                                  "<entry key=\"key1\">" +
                                    "<favoriteMovieXmlRootElement><title>&foo6;</title></favoriteMovieXmlRootElement>" +
                                  "</entry>" +
                                  "<entry key=\"key2\">" +
                                    "<favoriteMovieXmlRootElement><title>&foo6;</title></favoriteMovieXmlRootElement>" +
                                  "</entry>" +
                                "</map>";

   String bar = "<!DOCTYPE bar SYSTEM \"src/main/java/org/jboss/resteasy/resteasy1055/external.dtd\"><bar><s>junk</s></bar>";
   
   String filename = "src/main/java/org/jboss/resteasy/resteasy1055/testpasswd";
   
   String externalXmlRootElement = 
         "<?xml version=\"1.0\"?>\r" +
         "<!DOCTYPE foo\r" +
         "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
         "]>\r" + 
         "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>";
   
   String externalXmlType = 
         "<?xml version=\"1.0\"?>\r" +
         "<!DOCTYPE foo\r" +
         "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
         "]>\r" + 
         "<favoriteMovie><title>&xxe;</title></favoriteMovie>";
   
   String externalJAXBElement = 
         "<?xml version=\"1.0\"?>\r" +
         "<!DOCTYPE foo\r" +
         "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
         "]>\r" + 
         "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";
   
   String externalCollection = 
         "<?xml version=\"1.0\"?>\r" +
         "<!DOCTYPE foo\r" +
         "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
         "]>\r" + 
         "<collection>" +
         "  <favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
         "  <favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
         "</collection>";
   
   String externalMap = 
         "<?xml version=\"1.0\"?>\r" +
         "<!DOCTYPE foo\r" +
         "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
         "]>\r" + 
         "<map>" +
            "<entry key=\"american\">" +
               "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
            "</entry>" +
            "<entry key=\"french\">" +
               "<favoriteMovieXmlRootElement><title>&xxe;</title></favoriteMovieXmlRootElement>" +
            "</entry>" +
         "</map>";
   
   protected static String bigAttributeDoc;
   
   static
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<bar ");
      for (int i = 0; i < 12000; i++)
      {
         sb.append("attr" + i + "=\"x\" ");  
      }
      sb.append(">bar</bar>");
      bigAttributeDoc = sb.toString();
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testSecurityDefaultDTDsDefaultExpansionDefault() throws Exception
   {
      doTestSkipFailsFailsSkip("ddd");
   }

   @Test
   public void testSecurityDefaultDTDsDefaultExpansionFalse() throws Exception
   {
      doTestSkipFailsFailsSkip("ddf");
   }
   
   @Test
   public void testSecurityDefaultDTDsDefaultExpansionTrue() throws Exception
   {
      doTestSkipFailsFailsSkip("ddt");
   }
   
   @Test
   public void testSecurityDefaultDTDsFalseExpansionDefault() throws Exception
   {
      doTestFailsFailsPassesFails("dfd");
   }
  
   @Test
   public void testSecurityDefaultDTDsFalseExpansionFalse() throws Exception
   {
      doTestFailsFailsPassesFails("dff");
   }

   @Test
   public void testSecurityDefaultDTDsFalseExpansionTrue() throws Exception
   {
      doTestFailsFailsPassesPasses("dft");
   }
   
   @Test
   public void testSecurityDefaultDTDsTrueExpansionDefault() throws Exception
   {
      doTestSkipFailsFailsSkip("dtd");
   }
  
   @Test
   public void testSecurityDefaultDTDsTrueExpansionFalse() throws Exception
   {
      doTestSkipFailsFailsSkip("dtf");
   }

   @Test
   public void testSecurityDefaultDTDsTrueExpansionTrue() throws Exception
   {
      doTestSkipFailsFailsSkip("dtt");
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionDefault() throws Exception
   {
      doTestSkipPassesFailsSkip("fdd");
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionFalse() throws Exception
   {
      doTestSkipPassesFailsSkip("fdf");
   }
   
   @Test
   public void testSecurityFalseDTDsDefaultExpansionTrue() throws Exception
   {
      doTestSkipPassesFailsSkip("fdt");
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionDefault() throws Exception
   {
      doTestPassesPassesPassesFails("ffd");
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionFalse() throws Exception
   {
      doTestPassesPassesPassesFails("fff");
   }
   
   @Test
   public void testSecurityFalseDTDsFalseExpansionTrue() throws Exception
   {
      doTestPassesPassesPassesPasses("fft");
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionDefault() throws Exception
   {
      doTestSkipPassesFailsSkip("ftd");
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionFalse() throws Exception
   {
      doTestSkipPassesFailsSkip("ftf");
   }
   
   @Test
   public void testSecurityFalseDTDsTrueExpansionTrue() throws Exception
   {
      doTestSkipPassesFailsSkip("ftt");
   }
   
   @Test
   public void testSecurityTrueDTDsDefaultExpansionDefault() throws Exception
   {
      doTestSkipFailsFailsSkip("tdd");
   }

   @Test
   public void testSecurityTrueDTDsDefaultExpansionFalse() throws Exception
   {
      doTestSkipFailsFailsSkip("tdf");
   }
   
   @Test
   public void testSecurityTrueDTDsDefaultExpansionTrue() throws Exception
   {
      doTestSkipFailsFailsSkip("tdt");
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionDefault() throws Exception
   {
      doTestFailsFailsPassesFails("tfd");
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionFalse() throws Exception
   {
      doTestFailsFailsPassesFails("tff");
   }
   
   @Test
   public void testSecurityTrueDTDsFalseExpansionTrue() throws Exception
   {
      doTestFailsFailsPassesPasses("tft");
   }

   @Test
   public void testSecurityTrueDTDsTrueExpansionDefault() throws Exception
   {
      doTestSkipFailsFailsSkip("ttd");
   }
   
   @Test
   public void testSecurityTrueDTDsTrueExpansionFalse() throws Exception
   {
      doTestSkipFailsFailsSkip("ttf");
   }
   
   @Test
   public void testSecurityTrueDTDsTrueExpansionTrue() throws Exception
   {
      doTestSkipFailsFailsSkip("ttt");
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////
   void doTestSkipFailsFailsSkip(String ext) throws Exception
   {
      doMaxAttributesFails(ext);
      doDTDFails(ext);
   }
   
   void doTestSkipPassesFailsSkip(String ext) throws Exception
   {
      doMaxAttributesPasses(ext);
      doDTDFails(ext);
   }

   void doTestFailsFailsPassesFails(String ext) throws Exception
   {
      doEntityExpansionFails(ext);
      doMaxAttributesFails(ext);
      doDTDPasses(ext);
      doExternalEntityExpansionFails(ext);
   }

   void doTestFailsFailsPassesPasses(String ext) throws Exception
   {
      doEntityExpansionFails(ext);
      doMaxAttributesFails(ext);
      doDTDPasses(ext);
      doExternalEntityExpansionPasses(ext);
   }

   void doTestPassesPassesPassesFails(String ext) throws Exception
   {
      doEntityExpansionPasses(ext);
      doMaxAttributesPasses(ext);
      doDTDPasses(ext);
      doDTDPasses(ext);
      doExternalEntityExpansionFails(ext);
   }

   void doTestPassesPassesPassesPasses(String ext) throws Exception
   {
      doEntityExpansionPasses(ext);
      doMaxAttributesPasses(ext);
      doDTDPasses(ext);
      doDTDPasses(ext);
      doExternalEntityExpansionPasses(ext);
   }
   
   void doEntityExpansionFails(String ext) throws Exception
   {
      System.out.println("entering doEntityExpansionFails(" + ext + ")");
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlRootElement/");
         request.body("application/xml", bigXmlRootElement);
         System.out.println("bigXmlRootElement: " + bigXmlRootElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doEntityExpansionFails() result: " + entity);
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException"));
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlType/");
         request.body("application/xml", bigXmlType);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doEntityExpansionFails() result: " + entity);
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/JAXBElement/");
         request.body("application/xml", bigJAXBElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doEntityExpansionFails() result: " + entity);
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/collection/");
         request.body("application/xml", bigCollection);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doEntityExpansionFails() result: " + entity);
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/map/");
         request.body("application/xml", bigMap);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doEntityExpansionFails() result: " + entity);
         Assert.assertTrue(entity.contains("javax.xml.bind.UnmarshalException")); 
      }
   }
   
   void doEntityExpansionPasses(String ext) throws Exception
   {
      System.out.println("entering doEntityExpansionFails(" + ext + ")");
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlRootElement/");
         request.body("application/xml", bigXmlRootElement);
         System.out.println("bigXmlRootElement: " + bigXmlRootElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(1000000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlType/");
         request.body("application/xml", bigXmlType);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(1000000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/JAXBElement/");
         request.body("application/xml", bigJAXBElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(1000000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/collection/");
         request.body("application/xml", bigCollection);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(2000000, countFoos(entity));
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/map/");
         request.body("application/xml", bigMap);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(2000000, countFoos(entity));
      }
   }
   
   void doMaxAttributesFails(String ext) throws Exception
   {
      System.out.println("entering doMaxAttributesFails(" + ext + ")");
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/maxAttributes/");
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      System.out.println("doMaxAttributesFails() status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doMaxAttributesFails() result: " + entity);
//      Assert.assertEquals(400, response.getStatus());
//      Assert.assertTrue(entity.startsWith("javax.xml.bind.UnmarshalException"));
//      Assert.assertTrue(entity.contains("has more than \"10,000\" attributes")); 
   }

   void doMaxAttributesPasses(String ext) throws Exception
   {
      System.out.println("entering doMaxAttributesPasses(" + ext + ")");
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/maxAttributes/");
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      System.out.println("doMaxAttributesPasses() status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doMaxAttributesPasses() result: " + entity);
      Assert.assertEquals(204, response.getStatus());
//      Assert.assertEquals("bar", entity);
   }
   
   void doDTDFails(String ext) throws Exception
   {
      System.out.println("entering doDTDFails(" + ext + ")");
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/DTD/");
      request.body("application/xml", bar);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doDTDFails(): result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("org.xml.sax.SAXParseException"));
      Assert.assertTrue(entity.contains("DOCTYPE is disallowed"));  
   }
   
   void doDTDPasses(String ext) throws Exception
   {
      System.out.println("entering doDTDPasses(" + ext + ")");
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/DTD/");
      request.body("application/xml", bar);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doDTDPasses() result: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("junk", entity);
   }
   
   void doExternalEntityExpansionFails(String ext) throws Exception
   {
      System.out.println("entering doExternalEntityExpansionFails(" + ext + ")");
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlRootElement/");
         request.body("application/xml", externalXmlRootElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doExternalEntityExpansionFails() result: " + entity);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("", entity);
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlType/");
         request.body("application/xml", externalXmlType);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doExternalEntityExpansionFails() result: " + entity);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("", entity); 
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/JAXBElement/");
         request.body("application/xml", externalJAXBElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doExternalEntityExpansionFails() result: " + entity);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("", entity);; 
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/collection/");
         request.body("application/xml", externalCollection);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("doExternalEntityExpansionFails() result: " + entity);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("", entity);
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/map/");
         request.body("application/xml", externalMap);
         ClientResponse<?> response = request.post();
         String entity = response.getEntity(String.class);
         System.out.println("doExternalEntityExpansionFails() result: " + entity);
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("", entity);
      }
   }
   
   void doExternalEntityExpansionPasses(String ext) throws Exception
   {
      System.out.println("entering doExternalEntityExpansionPasses(" + ext + ")");
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlRootElement/");
         request.body("application/xml", externalXmlRootElement);
         System.out.println("bigXmlRootElement: " + bigXmlRootElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/xmlType/");
         request.body("application/xml", externalXmlType);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/JAXBElement/");
         request.body("application/xml", externalJAXBElement);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/collection/");
         request.body("application/xml", externalCollection);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx" + "xx:xx:xx:xx:xx:xx:xx", entity);
      }
      {
         ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1055-" + ext + "/entityExpansion/map/");
         request.body("application/xml", externalMap);
         ClientResponse<?> response = request.post();
         System.out.println("status: " + response.getStatus());
         String entity = response.getEntity(String.class);
         int len = Math.min(entity.length(), 30);
         System.out.println("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx" + "xx:xx:xx:xx:xx:xx:xx", entity);
      }
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
