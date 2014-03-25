package org.jboss.resteasy.test.fastinfoset.xxe;


import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.naming.spi.ObjectFactory;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.fastinfoset.xxe.generated.SearchType;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-659.
 *
 * Idea for test comes from Tim McCune:
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * This class is derived from org.jboss.resteasy.test.xxe.TestXXE.  There are two
 * differences:
 *
 * 1) The FastInfoset implementation does not expand external entity references, so
 *    the context parameter "resteasy.document.expand.entity.references" is ignored.
 *
 * 2) The FastInfoset provider does not support collections and maps.
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
public class TestXXE extends BaseResourceTest
{

   @Before
   public void before() throws Exception
   {
	   addPerRequestResource(SearchResource.class, Search.class, SearchType.class, ObjectFactory.class);
	   manualStart=true;
	   super.before();
	   addLibraryWithTransitiveDependencies(createGav());
   }


   private String createGav() {
	  String version=getVersion();
      String gav = "org.jboss.resteasy:resteasy-fastinfoset-provider:" + version;
	  return gav;
   }

   private String getVersion() {
       return readSystemProperty("version.org.jboss.resteasy", "2.3.7.Final");
   }

   private String readSystemProperty(String name, String defaultValue) {
       String value = System.getProperty(name);
       return (value == null) ? defaultValue : value;
   }

   @Test
   public void testXmlRootElementDefault() throws Exception
   {
      startContainer();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity(String.class).indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testXmlRootElementWithoutExpansion() throws Exception
   {
      contextParams.put("resteasy.document.expand.entity.references", "false");
	  startContainer();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity(String.class).indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testXmlRootElementWithExpansion() throws Exception
   {
      contextParams.put("resteasy.document.expand.entity.references", "true");
	  startContainer();
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ab", response.getEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testXmlTypeDefault() throws Exception
   {
      startContainer();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+fastinfoset", getSearchType());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testXmlTypeWithoutExpansion() throws Exception
   {
      contextParams.put("resteasy.document.expand.entity.references", "false");
	  startContainer();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+fastinfoset", getSearchType());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testXmlTypeWithExpansion() throws Exception
   {
      contextParams.put("resteasy.document.expand.entity.references", "true");
	  startContainer();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+fastinfoset", getSearchType());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testJAXBElementDefault() throws Exception
   {
      startContainer();
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testJAXBElementWithoutExpansion() throws Exception
   {
      contextParams.put("resteasy.document.expand.entity.references", "false");
	  startContainer();
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   @Test
   public void testJAXBElementWithExpansion() throws Exception
   {
      contextParams.put("resteasy.document.expand.entity.references", "true");
	  startContainer();
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
   }

   private static byte[] getSearch()
   {
      return new byte[] {
            (byte) 0xe0, 0x00, // header
                   0x00, 0x01, // version
                   0x00,       // flags
                   0x3c,       // literal
                   0x05,       // length - 1
                   's','e','a','r','c','h',
            (byte) 0xca,       // unexpanded entity reference
                   0x02,       // length - 1
                   'f', 'o', 'o',
                   0x18,       // length - 1
                   's','r','c','/','t','e','s','t','/','r','e','s','o','u','r','c','e','s','/','p','a','s','s','w','d',
            (byte) 0xff        // end of element, end of document
      };
   }

   private static byte[] getSearchType()
   {
      return new byte[] {
            (byte) 0xe0, 0x00, // header
                   0x00, 0x01, // version
                   0x00,       // flags
                   0x3c,       // literal
                   0x05,       // length - 1
                   's','e','a','r','c','h',
                   0x3c,       // literal
                   0x09,       // length
                   's','e','a','r','c','h','T','y','p','e',
            (byte) 0xca,       // unexpanded entity reference
                   0x02,       // length - 1
                   'f', 'o', 'o',
                   0x18,       // length - 1
                   's','r','c','/','t','e','s','t','/','r','e','s','o','u','r','c','e','s','/','p','a','s','s','w','d',
            (byte) 0xff,       // end of element, end of element
            (byte) 0xf0        // end of document, padding
      };
   }
}
