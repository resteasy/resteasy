package org.jboss.resteasy.test.fastinfoset.xxe;


import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Hashtable;

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
import org.jboss.resteasy.test.fastinfoset.xxe.generated.SearchType;
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
public class TestXXE
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("/")
   public static class SearchResource
   {
     @POST
     @Path("xmlRootElement")
     @Consumes({"application/*+fastinfoset"})
     public String addSearch(Search search)
     {
        System.out.println("SearchResource(xmlRootElment): search = " + search.getSearch());
        return concat("a", search.getSearch(), "b");
     }
     
     @POST
     @Path("xmlType")
     @Consumes({"application/*+fastinfoset"})
     public String addSearch(SearchType search)
     {
        System.out.println("SearchResource(xmlType): id = " + search.getId());
        return concat("a", search.getId(), "b");
     }
     
     @POST
     @Path("JAXBElement")
     @Consumes("application/*+fastinfoset")
     public String addSearch(JAXBElement<Search> value)
     {
        System.out.println("SearchResource(JAXBElement): search = " + value.getValue().getSearch());
        return concat("a", value.getValue().getSearch(), "b");
     }
   }
   
   @XmlRootElement(name="search")
   public static class Search {
     private String _search;
     public String getSearch() {
       return _search;
     }
     public void setSearch(String search) {
       _search = search;
     }
   }

   public static void before(String expandEntityReferences) throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("resteasy.document.expand.entity.references", expandEntityReferences);
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(SearchResource.class);
   }

   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(SearchResource.class);
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
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity(String.class).indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testXmlRootElementWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity(String.class).indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

   @Test
   public void testXmlRootElementWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/xmlRootElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ab", response.getEntity(String.class));
      after();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }

   @Test
   public void testXmlTypeDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+fastinfoset", getSearchType());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testXmlTypeWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/xmlType"));
      request.body("application/*+fastinfoset", getSearchType());
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
      request.body("application/*+fastinfoset", getSearchType());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementDefault() throws Exception
   {
      before();
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   @Test
   public void testJAXBElementWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/JAXBElement"));
      request.body("application/*+fastinfoset", getSearch());
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
      request.body("application/*+fastinfoset", getSearch());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
//      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0);
      Assert.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0);
      after();
   }
   
   private static String concat(String s1, Object o, String s2)
   {
      return s1 + (o == null ? "" : o.toString()) + s2;
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
